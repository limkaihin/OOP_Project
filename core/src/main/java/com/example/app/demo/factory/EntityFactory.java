package com.example.app.demo.factory;
import com.example.app.engine.entity.Entity;

public interface EntityFactory {
	Entity create();
	
	Entity create(float x, float y, String textureKey);

	Entity create(float x, float y, float vx, float vy, String textureKey, float w, float h);
}
