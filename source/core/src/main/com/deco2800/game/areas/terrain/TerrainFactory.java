package com.deco2800.game.areas.terrain;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.maps.tiled.renderers.HexagonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.deco2800.game.areas.terrain.TerrainComponent.TerrainOrientation;
import com.deco2800.game.components.CameraComponent;
import com.deco2800.game.utils.math.RandomUtils;
import com.deco2800.game.services.ResourceService;
import com.deco2800.game.services.ServiceLocator;

/** Factory for creating game terrains. */
public class TerrainFactory {
  private static final GridPoint2 MAP_SIZE = new GridPoint2(60, 60);
//  private static final int TUFT_TILE_COUNT = 20;
//  private static final int ROCK_TILE_COUNT = 20;

  private final OrthographicCamera camera;
  private final CameraComponent cameraComponent;
  private final TerrainOrientation orientation;

  /**
   * Create a terrain factory with Orthogonal orientation
   *
   * @param cameraComponent Camera to render terrains to. Must be orthographic.
   */
  public TerrainFactory(CameraComponent cameraComponent) {
    this(cameraComponent, TerrainOrientation.ORTHOGONAL);
  }

  /**
   * Create a terrain factory
   *
   * @param cameraComponent Camera to render terrains to. Must be orthographic.
   * @param orientation orientation to render terrain at
   */
  public TerrainFactory(CameraComponent cameraComponent, TerrainOrientation orientation) {
    this.camera = (OrthographicCamera) cameraComponent.getCamera();
    this.orientation = orientation;
    this.cameraComponent = cameraComponent;
  }

  public CameraComponent getCameraComponent() {
    return this.cameraComponent;
  }

  /**
   * Create a terrain of the given type, using the orientation of the factory. This can be extended
   * to add additional game terrains.
   *
   * @param terrainType Terrain to create
   * @return Terrain component which renders the terrain
   */

  public TerrainComponent createTerrain(TerrainType terrainType) {
    ResourceService resourceService = ServiceLocator.getResourceService();
    switch (terrainType) {
      case RAGNAROK_MAIN:
        TextureRegion background =
            new TextureRegion(resourceService.getAsset("images/Backgrounds/asgard_bg.png",
                Texture.class));
        return createRagnarockTerrain(0.5f, background);
      default:
        return null;
    }
  }

  private TerrainComponent createRagnarockTerrain(
      float tileWorldSize, TextureRegion background) {
    GridPoint2 tilePixelSize = new GridPoint2(background.getRegionWidth(), background.getRegionHeight());
    TiledMap tiledMap = createBackGndTiles(tilePixelSize, background);
    TiledMapRenderer renderer = createRenderer(tiledMap, 40*tileWorldSize/tilePixelSize.y);
    return new TerrainComponent(camera, tiledMap, renderer, orientation, tileWorldSize);
  }

  private TiledMapRenderer createRenderer(TiledMap tiledMap, float tileScale) {
    return new OrthogonalTiledMapRenderer(tiledMap, tileScale);
  }

  private TiledMap createBackGndTiles(
      GridPoint2 tileSize, TextureRegion background) {
    TiledMap tiledMap = new TiledMap();
    TerrainTile backgndTile = new TerrainTile(background);
    TiledMapTileLayer layer = new TiledMapTileLayer(MAP_SIZE.x, MAP_SIZE.y, tileSize.x, tileSize.y);

    // Create base background
    fillTiles(layer, MAP_SIZE, backgndTile);

    tiledMap.getLayers().add(layer);
    return tiledMap;
  }

  private static void fillTiles(TiledMapTileLayer layer, GridPoint2 mapSize, TerrainTile tile) {
    Cell cell = new Cell();
    cell.setTile(tile);
    layer.setCell(0, mapSize.x/40 - 1, cell);
  }

  /**
   * This enum should contain the different terrains in your game, e.g. forest, cave, home, all with
   * the same orientation. But for demonstration purposes, the base code has
   * the same level in 3
   * different orientations.
   */
  public enum TerrainType {
    RAGNAROK_MAIN
  }
}