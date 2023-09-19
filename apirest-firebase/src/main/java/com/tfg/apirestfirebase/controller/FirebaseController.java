package com.tfg.apirestfirebase.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.tfg.apirestfirebase.model.InfraccionData;

@RestController
public class FirebaseController {

	private static final Logger logger = LoggerFactory.getLogger(FirebaseController.class);
	
	@GetMapping("/usuarios/{userId}/matricula")
	public ResponseEntity<String> getMatriculaByUserId(@PathVariable String userId) throws InterruptedException, ExecutionException {
		DatabaseReference ref = FirebaseDatabase
				.getInstance("https://tfg-login-8ea38-default-rtdb.europe-west1.firebasedatabase.app")
				.getReference("usuarios/" + userId + "/matricula");

		CompletableFuture<String> future = new CompletableFuture<>();

		ref.addListenerForSingleValueEvent(new ValueEventListener() {
			@Override
			public void onDataChange(DataSnapshot dataSnapshot) {
				String matricula = dataSnapshot.getValue(String.class);
				future.complete(matricula);
			}

			@Override
			public void onCancelled(DatabaseError databaseError) {
				logger.info("Fallo al encontrar matricula: {}", databaseError.getMessage());
				future.completeExceptionally(databaseError.toException());
			}
		});

		String matricula = future.get();
		
		if (matricula != null) {
			logger.info("La matricula es: {}", matricula);
			return ResponseEntity.ok(matricula);
		} else {
			logger.info("No hay matricula");
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/usuarios/{userId}/infracciones")
    public List<InfraccionData> getInfraccionesByUserId(@PathVariable String userId) {
        DatabaseReference infraccionesRef = FirebaseDatabase
				.getInstance("https://tfg-login-8ea38-default-rtdb.europe-west1.firebasedatabase.app")
				.getReference("usuarios").child(userId).child("infracciones");
     
        CompletableFuture<List<InfraccionData>> future = new CompletableFuture<>();
        
        infraccionesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
            	List<InfraccionData> infracciones = new ArrayList<>();
                for (DataSnapshot infraccionSnapshot : dataSnapshot.getChildren()) {
                	Map<String,String> infraccion = (Map<String,String>) infraccionSnapshot.getValue();
                    InfraccionData infracciondata = new InfraccionData(
                    		infraccion.get("fecha"),
                    		infraccion.get("longitud"),
                    		infraccion.get("latitud"),
                    		infraccion.get("velocidad"),
                    		infraccion.get("velocidadMaxima"));
                    infracciones.add(infracciondata);
                }
                
                future.complete(infracciones);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            	future.completeExceptionally(databaseError.toException());
            }
        });
        
        List<InfraccionData> infracciones = new ArrayList<>();
        
        try {
        	infracciones = future.get();
        	logger.info("infracciones: {}", infracciones);
        	return infracciones;
        } catch(Exception e) {
        	logger.info("fallo con future: {}", e.getMessage());
        	return infracciones;
        }
        
    }

	
	@PostMapping("/usuarios/{userId}/matricula")
	public ResponseEntity<Void> setMatriculaByUserId(@PathVariable String userId, @RequestBody String matricula) throws InterruptedException, ExecutionException {
		DatabaseReference ref = FirebaseDatabase
				.getInstance("https://tfg-login-8ea38-default-rtdb.europe-west1.firebasedatabase.app")
				.getReference("usuarios");
		try {
			ref.child(userId).child("matricula").setValue(matricula, null);
			logger.info("La matricula se ha cambiado: {}", matricula);
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			logger.info("Error al cambiar matricula: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	@PostMapping("/usuarios/{userId}/infracciones")
	public ResponseEntity<Void> setInfraccionByUserId(@PathVariable String userId, @RequestBody InfraccionData infraccionData) throws InterruptedException, ExecutionException {
		DatabaseReference ref = FirebaseDatabase
				.getInstance("https://tfg-login-8ea38-default-rtdb.europe-west1.firebasedatabase.app")
				.getReference("usuarios");
		try {
			DatabaseReference referenceInfracciones = ref.child(userId).child("infracciones");
			
			String nuevaInfraccionKey = referenceInfracciones.push().getKey();
			Map<String, Object> infracionValues = new HashMap<>();
			infracionValues.put("fecha", infraccionData.getFecha());
			infracionValues.put("latitud", infraccionData.getLatitud());
			infracionValues.put("longitud", infraccionData.getLongitud());
			infracionValues.put("velocidad", infraccionData.getVelocidad());
			infracionValues.put("velocidadMaxima", infraccionData.getVelocidadMaxima());
			
			referenceInfracciones.child(nuevaInfraccionKey)
			.setValue(infracionValues, null);
			
			logger.info("La infraccion se ha guardado correctamente");
			return ResponseEntity.ok().build();
		} catch (Exception e) {
			logger.info("Error al guardar la infraccion: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
}
