package com.example.app.demo.factory;
import com.example.app.engine.entity.Entity;

public interface EntityFactory {
	Entity create();
	
	Entity create(float x, float y, String textureKey);
}
