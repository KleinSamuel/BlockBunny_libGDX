package handler;

import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

public class Content {

	private HashMap<String, Texture> textures;
	private HashMap<String, Music> music;
	private HashMap<String, Sound> sounds;
	
	public Content(){
		textures = new HashMap<>();
		music = new HashMap<>();
		sounds = new HashMap<>();
	}
	
	public void loadTexture(String path){
		int slashIndex = path.lastIndexOf("/");
		String key;
		
		if(slashIndex == -1){
			key = path.substring(0, path.lastIndexOf("."));
		}else{
			key = path.substring(slashIndex + 1, path.lastIndexOf("."));
		}
		loadTexture(path, key);
	}
	
	public void loadTexture(String path, String key){
		Texture tex = new Texture(Gdx.files.internal(path));
		textures.put(key, tex);
	}
	
	public Texture getTexture(String key){
		return textures.get(key);
	}
	
	public void disposeTexture(String key){
		Texture tex = textures.get(key);
		if(tex != null){
			tex.dispose();
		}
	}
	
	public void loadMusic(String path) {
		int slashIndex = path.lastIndexOf('/');
		String key;
		if(slashIndex == -1) {
			key = path.substring(0, path.lastIndexOf('.'));
		}
		else {
			key = path.substring(slashIndex + 1, path.lastIndexOf('.'));
		}
		loadMusic(path, key);
	}
	public void loadMusic(String path, String key) {
		Music m = Gdx.audio.newMusic(Gdx.files.internal(path));
		music.put(key, m);
	}
	public Music getMusic(String key) {
		return music.get(key);
	}
	public void removeMusic(String key) {
		Music m = music.get(key);
		if(m != null) {
			music.remove(key);
			m.dispose();
		}
	}
	
	public void loadSound(String path) {
		int slashIndex = path.lastIndexOf('/');
		String key;
		if(slashIndex == -1) {
			key = path.substring(0, path.lastIndexOf('.'));
		}
		else {
			key = path.substring(slashIndex + 1, path.lastIndexOf('.'));
		}
		loadSound(path, key);
	}
	public void loadSound(String path, String key) {
		Sound sound = Gdx.audio.newSound(Gdx.files.internal(path));
		sounds.put(key, sound);
	}
	public Sound getSound(String key) {
		return sounds.get(key);
	}
	public void removeSound(String key) {
		Sound sound = sounds.get(key);
		if(sound != null) {
			sounds.remove(key);
			sound.dispose();
		}
	}
	
	public void removeAll() {
		for(Object o : textures.values()) {
			Texture tex = (Texture) o;
			tex.dispose();
		}
		textures.clear();
		for(Object o : music.values()) {
			Music music = (Music) o;
			music.dispose();
		}
		music.clear();
		for(Object o : sounds.values()) {
			Sound sound = (Sound) o;
			sound.dispose();
		}
		sounds.clear();
	}
}
