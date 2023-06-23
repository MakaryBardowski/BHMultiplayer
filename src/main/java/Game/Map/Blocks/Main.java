package Game.Map.Blocks;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.ui.Picture;
import java.util.Arrays;

/**
 * This is the Main Class of your Game. You should only do initialization here.
 * Move your Logic into AppStates or Controls
 * @author normenhansen
 */
public class Main extends SimpleApplication {
BitmapText nodeCountText;
    BlockWorld bw;
    int cbx,cby,cbz;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    public void simpleInitApp() {
        viewPort.setBackgroundColor(ColorRGBA.Cyan);
        flyCam.setMoveSpeed(70);
        addLabel();
        initKeys();
        
//         bw = new BlockWorld(5,16,48 ,assetManager,rootNode);
        
        
        




//bw.placeBlock(0, 0, 0,BlockType.DIRT);
//bw.placeBlock(1, 0, 0,BlockType.STONE);
////bw.placeBlock(2, 0, 0,BlockType.STONE);
////bw.placeBlock(3, 0, 0,BlockType.STONE);
//
//bw.placeBlock(0, 0, 1,BlockType.DIRT);
//bw.placeBlock(1, 0, 1,BlockType.STONE);
//
//bw.placeBlock(0, 2, 0,BlockType.STONE);
//bw.placeBlock(1, 2, 0,BlockType.DIRT);
//bw.placeBlock(0, 2, 1,BlockType.STONE);
//bw.placeBlock(1, 2, 1,BlockType.DIRT);
//bw.placeBlock(9, 3, 4,BlockType.STONE);


    }

    @Override
    public void simpleUpdate(float tpf) {
        updateLighting();
//         castRay();
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
    
    
    
    private void addLabel(){
        nodeCountText = new BitmapText(guiFont, false);
        nodeCountText.setSize(guiFont.getCharSet().getRenderedSize() * 2);
        nodeCountText.setText("Swiatlo: ");
        nodeCountText.setLocalTranslation(
                settings.getWidth() / 10 - nodeCountText.getLineWidth() / 2,
                settings.getHeight() - settings.getHeight() / 15 + nodeCountText.getLineHeight() / 2, 0);
        guiNode.attachChild(nodeCountText);
    
        
        
          Picture crosshair = new Picture("crosshair");
        crosshair.setImage(assetManager, "Textures/crosshair.png", true);
        crosshair.setWidth(settings.getHeight() * 0.04f);
        crosshair.setHeight(settings.getHeight() * 0.04f); //0.04f
        crosshair.setPosition((settings.getWidth() / 2) - settings.getHeight() * 0.04f / 2, settings.getHeight() / 2 - settings.getHeight() * 0.04f / 2);
        guiNode.attachChild(crosshair);
    }
    
    private void castRay(){
    
         CollisionResults results = new CollisionResults();
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        rootNode.collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult closest = results.getClosestCollision();
            Vector3f cpt = closest.getContactPoint().clone();
            int x = (int) Math.floor(cpt.getX() / 5);
            int y = (int) Math.floor((cpt.clone().getY() + 0.02f) / 5);
            int z = (int) Math.floor(cpt.getZ() / 5);
//                    System.out.println("closest: x" + bx + " y " + by + " z " + bz);
            if (x >= 0 && y >= 0 && z >= 0 && x < bw.getMap().length && y < bw.getMap()[0].length && z < bw.getMap()[0][0].length) {
          
                
                      int chunkX = 16 * (int) (Math.floor(x / 16));
                    int chunkZ = 16 * (int) (Math.floor(z / 16));
                
                
               if(bw.getMap()[x][y][z] != null)
//                   nodeCountText.setText("block vertex offset: "+Arrays.toString(bw.getMap()[x][y][z].getFaceOffsetIndexes()));

nodeCountText.setText("block vertex offset: "+bw.getMap()[x][y][z].getVertexOffsetInParentChunk()+"\n"+
        bw.getMap()[x][y][z].getVertexCount()
//        +"\n"+bw.getMap()[x][y][z].getIndices()
        
        );

               
            }

        }
    }
    
    
    
    public void updateLighting(){
            int bx = (int) Math.floor(cam.getLocation().getX() / bw.getBLOCK_SIZE());
        int by = (int) Math.floor(cam.getLocation().getY() / bw.getBLOCK_SIZE());
        int bz = (int) Math.floor(cam.getLocation().getZ() / bw.getBLOCK_SIZE());
                    castRay();

        if (bx != cbx || by != cby || bz != cbz) {

//        throw new NullPointerException();
//            long t = System.currentTimeMillis();
            int range = 3;
            int height = 6; //6
            int lpos = 0;
            for (int i = lpos; i < height; i++) {
                VoxelLighting.deleteLight(cbx, i, cbz, bw.getMap(), range);
            }

            for (int i = lpos; i < height; i++) {
                VoxelLighting.updateBlockState(cbx, i, cbz, bw.getMap(), range, bw.getChunks());
            }

            cbx = bx;
            cby = by;
            cbz = bz;
            for (int i = lpos; i < height; i++) {
                VoxelLighting.addLight(cbx, i, cbz, bw.getMap(), range);
            }

            for (int i = lpos; i < height; i++) {
                VoxelLighting.updateBlockState(cbx, i, cbz, bw.getMap(), range, bw.getChunks());
            }
//            System.out.println(System.currentTimeMillis() - t + "ms");
        }
    
    }
    
    
    
    
    
    
    
    
       final private ActionListener actionListener = new ActionListener() {
        @Override
        public void onAction(String name, boolean keyPressed, float tpf) {
          
            if (name.equals("Shoot") && !keyPressed) {
                
                 CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                rootNode.collideWith(ray, results);

                if (results.size() > 0) {
                    CollisionResult closest = results.getClosestCollision();
                    Vector3f cpt = closest.getContactPoint().clone();
                    int ux = (int) Math.floor(cpt.getX() / bw.getBLOCK_SIZE());
                    int uy = (int) Math.floor((cpt.clone().getY() + 0.2f) / bw.getBLOCK_SIZE());
                    int uz = (int) Math.floor(cpt.getZ() / bw.getBLOCK_SIZE());
                    
                    bw.placeBlock(ux, uy, uz, BlockType.STONE);

                }

            }

            if (name.equals("Remove") && !keyPressed) {
                CollisionResults results = new CollisionResults();
                Ray ray = new Ray(cam.getLocation(), cam.getDirection());
                rootNode.collideWith(ray, results);

                if (results.size() > 0) {
                    CollisionResult closest = results.getClosestCollision();
                    Vector3f cpt = closest.getContactPoint().clone();
                    int ux = (int) Math.floor(cpt.getX() / bw.getBLOCK_SIZE());
                    int uy = (int) Math.floor((cpt.clone().getY() - 0.2f) / bw.getBLOCK_SIZE());
                    int uz = (int) Math.floor(cpt.getZ() / bw.getBLOCK_SIZE());
                    
                    long t1 = System.currentTimeMillis();
                    
                    if(ux >= 0 && uy >= 0 && uz >=0){
                    bw.removeBlock(ux,uy,uz);
                    System.out.println(System.currentTimeMillis()-t1+"ms passed during block removal");
                }
                }
            }

 
        }
    };
    
    
    
    
    
    
    
    
    
       private void initKeys() {
        inputManager.addMapping("R",
                new KeyTrigger(KeyInput.KEY_R)); // trigger 1: spacebar

        inputManager.addMapping("Shoot",
                // trigger 1: spacebar
                new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
        inputManager.addMapping("Remove",
                new KeyTrigger(KeyInput.KEY_SPACE)); // trigger 1: spacebar

        inputManager.addMapping("placeAir",
                new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)); // trigger 2: left-button click

        inputManager.addListener(actionListener, "Shoot");
        inputManager.addListener(actionListener, "placeAir");

        inputManager.addListener(actionListener, "R");
        inputManager.addListener(actionListener, "Remove");

    }
    
    
    
    
    
}
