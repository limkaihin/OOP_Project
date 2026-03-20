package com.example.app.engine.factory;

import com.example.app.engine.entity.Entity;

public interface EntityFactory {
	Entity create();

	Entity create(float x, float y, String textureKey);
}