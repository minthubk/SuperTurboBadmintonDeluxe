package de.redlion.badminton;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import de.redlion.badminton.controls.SinglePlayerControls;
import de.redlion.badminton.opponent.AIOpponent;
import de.redlion.badminton.opponent.Opponent;
import de.redlion.badminton.render.RenderDebug;
import de.redlion.badminton.render.RenderStadium;

public class SinglePlayerGameScreen extends DefaultScreen {

	float startTime = 0;
	PerspectiveCamera cam;

	SpriteBatch batch;
	SpriteBatch fadeBatch;
	Sprite blackFade;
	
	BitmapFont font;
	
	RenderStadium renderStadium;
	RenderDebug renderDebug;

	Player player = GameSession.getInstance().player;
	Birdie birdie = GameSession.getInstance().birdie;
	AIOpponent opponent;

	float fade = 1.0f;
	boolean finished = false;

	float delta;

	public SinglePlayerGameScreen(Game game) {
		super(game);
		
		GameSession.getInstance().newSinglePlayerGame();
		
		//refresh references 
		//TODO Observer Pattern for newGame
		opponent = (AIOpponent) GameSession.getInstance().opponent;
		renderStadium = new RenderStadium();
		renderDebug = new RenderDebug();
		
		Gdx.input.setInputProcessor(new SinglePlayerControls(player));

		batch = new SpriteBatch();
		batch.getProjectionMatrix().setToOrtho2D(0, 0, 800, 480);
		
		blackFade = new Sprite(
				new Texture(Gdx.files.internal("data/black.png")));
		fadeBatch = new SpriteBatch();
		fadeBatch.getProjectionMatrix().setToOrtho2D(0, 0, 1, 1);
		
		font = Resources.getInstance().font;
		font.setScale(1);
		
		cam = new PerspectiveCamera(7, Gdx.graphics.getWidth(),	Gdx.graphics.getHeight());
		cam.lookAt(0, 0, 0.5f);

		initRender();
	}

	public void initRender() {
		Gdx.graphics.getGL20().glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
		Gdx.gl.glActiveTexture(GL20.GL_TEXTURE0);
		Gdx.gl.glClearColor(0.2f ,0.2f ,0.2f ,1.0f);
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
		cam.viewportWidth = width;
		cam.viewportHeight = height;
		
		renderDebug.resize(width, height);
		
		initRender();
	}

	@Override
	public void show() {
	}
	
	private float deltaCount = 0;	
	@Override
	public void render(float deltaTime) {
		deltaCount += deltaTime;
		if(deltaCount > 0.01) {
			deltaCount = 0;
			renderFrame(0.02f);
		}
	}

	public void renderFrame(float deltaTime) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		delta = Math.min(0.1f, deltaTime);

		startTime += delta;

		cam.update();

		collisionTest();
		updateAI();
		
		cam.position.set(0, 26f, 58f);
		cam.fieldOfView = 14;
		cam.lookAt(0, 0.0f, GameSession.getInstance().birdie.currentPosition.y / 10);
		cam.up.set(0, 1, 0);
		cam.near = 0.5f;
		cam.far = 1000f;
		renderStadium.updateCamera(cam);
		if(!Configuration.getInstance().debug) {
			renderStadium.render();
		}

		batch.begin();
		font.draw(batch, "P " + GameSession.getInstance().playerScore + " : O "
				+ GameSession.getInstance().opponentScore, 720, 30);
		batch.end();

		if (Configuration.getInstance().debug) {
			renderDebug.render(cam);
		}

		// FadeInOut
		if (!finished && fade > 0) {
			fade = Math.max(fade - (delta), 0);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g,
					blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
		}

		if (finished) {
			fade = Math.min(fade + (delta), 1);
			fadeBatch.begin();
			blackFade.setColor(blackFade.getColor().r, blackFade.getColor().g,
					blackFade.getColor().b, fade);
			blackFade.draw(fadeBatch);
			fadeBatch.end();
			if (fade >= 1) {
				Gdx.app.exit();
			}
		}
	}

	private void updateAI() {
		player.update();
		birdie.update();
		opponent.update(player.position);

		if (opponent.position.dst(birdie.currentPosition) < 4.0f
				&& birdie.state != Birdie.STATE.HITBYOPPONENT) {

			int randomAim = MathUtils.random(0, 3);
			if(randomAim == 0) opponent.aiming = Player.AIMING.LEFT;
			if(randomAim == 1) opponent.aiming = Player.AIMING.RIGHT;
			if(randomAim == 2) opponent.aiming = Player.AIMING.UPLEFT;
			if(randomAim == 3) opponent.aiming = Player.AIMING.UPRIGHT;
			
			birdie.hit(opponent,false);
			
			opponent.switchState();
		}
	}

	private void collisionTest() {
		if(birdie.state == Birdie.STATE.HELD) {
			return;
		}

		// check if player is in aiming mode and could hit birdie
		if (player.state == Player.STATE.AIMING
				&& player.position.dst(birdie.currentPosition) < 4.0f
				&& birdie.state != Birdie.STATE.HIT) {
			birdie.hit(player, false);
			
			player.switchState();
		}

	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

}
