package com.jukusoft.mmo.engine.gameview.camera;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.jukusoft.mmo.engine.applayer.time.GameTime;
import com.jukusoft.mmo.engine.shared.utils.PlatformUtils;
import com.jukusoft.mmo.engine.gameview.camera.impl.Shake1CameraModification;
import com.jukusoft.mmo.engine.gameview.camera.impl.Shake2CameraModification;
import com.jukusoft.mmo.engine.gameview.camera.impl.Shake3CameraModification;
import com.jukusoft.mmo.engine.gameview.utils.MouseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Justin on 09.09.2017.
 */
public class CameraHelper implements ModificationFinishedListener {

    protected static final String CLASS_CANNOT_BE_NULL = "class cannot be null.";

    //libGDX 2D orthographic camera
    protected OrthographicCamera camera = null;

    //camera position
    protected float x = 0;
    protected float y = 0;
    protected float zoom = 1;

    //camera offset
    protected float cameraOffsetX = 0;
    protected float cameraOffsetY = 0;

    //camera dimension
    protected int width = 0;
    protected int height = 0;

    //camera bounds
    protected float minX = -Float.MAX_VALUE;
    protected float maxX = Float.MAX_VALUE;
    protected float minY = -Float.MAX_VALUE;
    protected float maxY = Float.MAX_VALUE;

    //target camera position
    protected float targetX = 0;
    protected float targetY = 0;

    //camera mode
    protected CameraMode mode = CameraMode.DIRECT_CAMERA;

    //list with resize listeners
    protected List<ResizeListener> resizeListeners = new ArrayList<>();

    //temporary vector to avoid creation of new vector in gameloop
    protected Vector3 tmpScreenVector = new Vector3(0, 0, 0);

    //camera modifications
    protected Map<Class, CameraModification> cameraModificationMap = new ConcurrentHashMap<>();
    protected List<CameraModification> activeModifications = new ArrayList<>();

    //temporary camera params (to avoid override values)
    protected TempCameraParams tempCameraParams = null;

    //border padding on which mouse scrolling works
    protected int mouseScrollBorder = 50;

    //speed of mouse scrolling
    protected float mouseScrollSpeed = 5;

    //viewport
    protected Viewport viewport = null;

    /**
    * default constructor
     *
     * @param width viewport width
     * @param height viewport height
    */
    public CameraHelper (int width, int height) {
        this.width = width;
        this.height = height/* - 30*/;

        //create new libGDX 2D orthographic camera
        this.camera = new OrthographicCamera(width, height);

        //move camera, so (0, 0) is on left bottom
        this.camera.translate(width / 2f, height / 2f, 0);
        this.camera.update();

        calculateOffset();

        //create new temp camera params
        this.tempCameraParams = new TempCameraParams(this.x, this.y, 1);

        //add some basic camera modifications
        this.registerMod(new Shake1CameraModification(), Shake1CameraModification.class);
        this.registerMod(new Shake2CameraModification(), Shake2CameraModification.class);
        this.registerMod(new Shake3CameraModification(), Shake3CameraModification.class);
    }

    /**
    * calculate offset position, so that (0, 0) is on left bottom corner
    */
    public void calculateOffset () {
        this.cameraOffsetX = getViewportWidth() / 2f;
        this.cameraOffsetY = getViewportHeight() / 2f;
    }

    /**
    * translate camera
     *
     * @param x x
     * @param y y
     * @param zoom zoom
    */
    public void translate(float x, float y, float zoom) {
        this.targetX += x;
        this.targetY += y;
        this.zoom += zoom;
    }

    public float getX () {
        return this.x;
    }

    public float getY () {
        return this.y;
    }

    public float getZoom () {
        return this.zoom;
    }

    public void setZoom(float zoom) {
        this.zoom = zoom;

        this.syncPosToCamera();
    }

    /**
    * get viewport
     *
     * @return instance of viewport
    */
    public Viewport getViewport () {
        return this.viewport;
    }

    /**
    * get viewport width
     *
     * @return viewport width
    */
    public int getViewportWidth () {
        return this.width;
    }

    /**
     * get viewport height
     *
     * @return viewport height
     */
    public int getViewportHeight () {
        return this.height;
    }

    /**
    * resize camera viewport (set new dimension)
     *
     * @param width viewport width
     * @param height viewport height
    */
    public void resize (int width, int height) {
        Gdx.app.debug("CameraHelper", "resize, new width: " + width + ", new height: " + height);

        //set new width and height
        this.width = width;
        this.height = height;

        //calculate offset
        this.calculateOffset();

        //call resize listeners
        this.resizeListeners.stream().forEach(consumer -> consumer.onResize(width, height));

    }

    public void addResizeListener(ResizeListener listener) {
        this.resizeListeners.add(listener);
    }

    public void removeResizeListener(ResizeListener listener) {
        this.resizeListeners.remove(listener);
    }

    /**
    * update camera
     *
     * @param time game time
    */
    public void update (GameTime time) {
        //TODO: check, if camera can scroll

        //save old values
        float lastX = this.x;
        float lastY = this.y;

        //move camera to target position
        if (mode == CameraMode.DIRECT_CAMERA) {
            //set x and y position directly to target position
            this.x = this.targetX;
            this.y = this.targetY;
        } else if (mode == CameraMode.SMOOTH_CAMERA) {
            //TODO: add code here
        } else if (mode == CameraMode.FIXED_CAMERA) {
            //dont move camera
        } else if (mode == CameraMode.MOUSE_CAMERA) {
            //get current mouse positon
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();

            if (mouseX < 0 + this.mouseScrollBorder) {
                //scroll left
                this.x = this.x - this.mouseScrollSpeed;
            } else if (mouseX > getViewportWidth() - this.mouseScrollBorder) {
                //scroll right
                this.x = this.x + this.mouseScrollSpeed;
            }

            if (mouseY < 0 + this.mouseScrollBorder) {
                //scroll down
                this.y = this.y - this.mouseScrollSpeed;
            } else if (mouseY > getViewportHeight() - this.mouseScrollBorder) {
                //scroll up
                this.y = this.y + this.mouseScrollSpeed;
            }
        } else if (mode == CameraMode.SCROLL_CAMERA_WITH_MAX_DISTANCE) {
            //TODO: add code here
        } else {
            //throw exception
            throw new IllegalStateException("Unknown camera mode: " + mode.name());
        }

        //check, if new position is in bounds, if not correct position
        if (this.mode != CameraMode.FIXED_CAMERA) {
            //check, if camera can scroll
            float deltaX = this.x - lastX;
            float deltaY = this.y - lastY;

            //check, if camera can scroll on x axis and is in bounds
            if (!this.canScrollX(deltaX)) {
                if (this.x + deltaX < this.minX) {
                    this.targetX = this.minX;
                    this.x = this.minX;
                } else if (this.x + deltaX + width > this.maxX) {
                    if ((maxX - minX) < width) {
                        //set min position, because bounds width is smaller than window width
                        this.x = minX;
                        this.targetX = minX;
                    } else {
                        this.targetX = this.maxX - width;
                        this.x = this.maxX - width;
                    }
                }
            }

            //check, if camera can scroll on y axis and is in bounds
            if (!this.canScrollY(deltaY)) {
                if (this.y + deltaY < this.minY) {
                    this.targetY = this.minY;
                    this.y = this.minY;
                } else if (this.y + deltaY + height > this.maxY) {
                    if ((maxY - minY) < height) {
                        //set min position, because bounds height is smaller than window height
                        this.y = minY;
                        this.targetY = minY;
                    } else {
                        this.targetY = this.maxY - height;
                        this.y = this.maxY - height;
                    }
                }
            }
        }

        //reset temporary camera position
        this.tempCameraParams.reset(getX(), getY(), getZoom());

        //update modifications first
        for (CameraModification mod : this.activeModifications) {
            mod.onUpdate(time, this.tempCameraParams, this);
        }

        //sync camera helper position to libGDX camera
        this.syncPosToCamera();

        //update libGDX camera
        this.camera.update();
    }

    /**
    * get mode
     *
     * @return current camera mode
    */
    public CameraMode getMode () {
        return this.mode;
    }

    /**
    * set camera mode
     *
     * @param mode camera mode
    */
    public void setMode (CameraMode mode) {
        this.mode = mode;
    }

    /**
    * get target camera positon x
     *
     * @return target x position
    */
    public float getTargetX () {
        return this.targetX;
    }

    /**
    * set target camera position x
     *
     * @param targetX target camera position x
    */
    public void setTargetX (float targetX) {
        this.targetX = targetX;
    }

    /**
     * get target camera positon y
     *
     * @return target y position
     */
    public float getTargetY () {
        return this.targetY;
    }

    /**
     * set target camera position y
     *
     * @param targetY target camera position y
     */
    public void setTargetY (float targetY) {
        this.targetY = targetY;
    }

    /**
    * set target camera position
     *
     * @param targetX target x position
     * @param targetY target y position
    */
    public void setTargetPos (float targetX, float targetY) {
        this.targetX = targetX;
        this.targetY = targetY;
    }

    public void forcePos (float x, float y) {
        this.targetX = x;
        this.targetY = y;
        this.x = x;
        this.y = y;

        //sync camera helper position to libGDX camera
        this.syncPosToCamera();

        //update libGDX camera
        this.camera.update();
    }

    /**
    * set target position in middle of viewport
     *
     * @param targetX target x position in middle of viewport
     * @param targetY target y position in middle of viewport
    */
    public void setTargetMiddlePos (float targetX, float targetY) {
        this.targetX = targetX - (this.getViewportWidth() / 2f);
        this.targetY = targetY - (this.getViewportHeight() / 2f);
    }

    /**
    * get camera offset x
     *
     * @return camera offset x
    */
    public float getOffsetX () {
        return this.cameraOffsetX;
    }

    /**
     * get camera offset y
     *
     * @return camera offset y
     */
    public float getOffsetY () {
        return this.cameraOffsetY;
    }

    /**
    * check, if camera can scroll on x axis (and is in bounds)
     *
     * @param deltaX value which is added to current x position to check
     *
     * @return true, if camera can scroll on x axis
    */
    public boolean canScrollX (float deltaX) {
        float newX = this.x + deltaX;

        float x2 = newX + getViewportWidth();

        return newX >= this.minX && x2 <= this.maxX;
    }

    /**
     * check, if camera can scroll on y axis (and is in bounds)
     *
     * @param deltaY value which is added to current x position to check
     *
     * @return true, if camera can scroll on y axis
     */
    public boolean canScrollY (float deltaY) {
        float newY = this.y + deltaY;

        float y2 = newY + getViewportHeight();

        return newY >= this.minY && y2 <= this.maxY;
    }

    /**
    * set bounds for x coordinate
     *
     * @param minX minimum x value
     * @param maxX maximum x value
     */
    public void setXBounds (float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    /**
     * set bounds for y coordinate
     *
     * @param minY minimum x value
     * @param maxY maximum x value
     */
    public void setYBounds (float minY, float maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * set camera bounds
     *
     * @param minX minimum x value
     * @param maxX maximum x value
     * @param minY minimum y value
     * @param maxY maximum y value
     */
    public void setBounds (float minX, float maxX, float minY, float maxY) {
        this.setXBounds(minX, maxX);
        this.setYBounds(minY, maxY);
    }

    /**
    * reset camera position &amp; target position
    */
    public void resetPosition () {
        this.x = 0;
        this.y = 0;
        this.zoom = 1;

        //reset target position
        this.targetX = 0;
        this.targetY = 0;
    }

    /**
    * reset camera bounds
    */
    public void resetBounds () {
        this.minX = -Float.MAX_VALUE;
        this.maxX = Float.MAX_VALUE;
        this.minY = -Float.MAX_VALUE;
        this.maxY = Float.MAX_VALUE;
    }

    /**
    * reset camera
    */
    public void reset () {
        //reset position
        this.resetPosition();

        //reset bounds
        this.resetBounds();

        //reset camera mode
        this.mode = CameraMode.DIRECT_CAMERA;
    }

    /**
    * sync camera helper position to libGDX original camera
    */
    protected void syncPosToCamera() {
        //set libGDX camera dimension
        this.camera.viewportWidth = this.width;
        this.camera.viewportHeight = this.height;

        //set camera position to libGDX camera
        this.camera.position.x = this.tempCameraParams.getX() + cameraOffsetX;
        this.camera.position.y = this.tempCameraParams.getY() + cameraOffsetY;
        this.camera.zoom = this.tempCameraParams.getZoom();
    }

    /**
    * get combined Matrix4 for libGDX SpriteBatch
     *
     * @return combined matrix
    */
    public Matrix4 getCombined() {
        return this.camera.combined;
    }

    /**
    * get view matrix
     *
     * @return view matrix
    */
    public Matrix4 getViewMatrix () {
        return this.camera.view;
    }

    /**
    * get projection matrix
     *
     * @return projection matrix
    */
    public Matrix4 getProjectionMatrix () {
        return this.camera.projection;
    }

    /**
    * get frustum
     *
     * @return frustum
    */
    public Frustum getFrustum() {
        return this.camera.frustum;
    }

    /**
    * get mouse position in camera
     *
     * @return current mouse position
    */
    public Vector3 getMousePosition() {
        this.tmpScreenVector.set(Gdx.input.getX(), Gdx.input.getY(), 0);

        this.tmpScreenVector = camera.unproject(this.tmpScreenVector);

        if (!Gdx.graphics.isFullscreen() && Gdx.app.getType() == Application.ApplicationType.Desktop && PlatformUtils.isWindows()) {
            this.tmpScreenVector.y = MouseUtils.correctDecoredMousePositionY(this.tmpScreenVector.y);
        }

        return this.tmpScreenVector;
    }

    @Override
    public <T extends CameraModification> void onModificationFinished(T mod, Class<T> cls) {
        if (mod == null) {
            throw new NullPointerException("mod cannot be null.");
        }

        this.deactivateMod(cls);
    }

    public <T extends CameraModification> void registerMod(T mod, Class<T> cls) {
        if (mod == null) {
            throw new NullPointerException("mod cannot be null.");
        }

        if (cls == null) {
            throw new NullPointerException(CLASS_CANNOT_BE_NULL);
        }

        this.cameraModificationMap.put(cls, mod);
    }

    public <T extends CameraModification> void removeMod(Class<T> cls) {
        if (cls == null) {
            throw new NullPointerException(CLASS_CANNOT_BE_NULL);
        }

        CameraModification mod = this.cameraModificationMap.get(cls);

        if (mod != null) {
            this.activeModifications.remove(mod);
        }

        this.cameraModificationMap.remove(cls);
    }

    public <T extends CameraModification> T getMod(Class<T> cls) {
        if (cls == null) {
            throw new NullPointerException(CLASS_CANNOT_BE_NULL);
        }

        CameraModification mod = this.cameraModificationMap.get(cls);

        if (mod == null) {
            return null;
        }

        return cls.cast(mod);
    }

    public <T extends CameraModification> void activateMod(Class<T> cls) {
        if (cls == null) {
            throw new NullPointerException(CLASS_CANNOT_BE_NULL);
        }

        CameraModification mod = this.cameraModificationMap.get(cls);

        if (mod == null) {
            throw new IllegalStateException("camera modification is not registered: " + cls.getName());
        }

        if (!this.activeModifications.contains(mod)) {
            this.activeModifications.add(mod);
        }
    }

    public <T extends CameraModification> void deactivateMod(Class<T> cls) {
        if (cls == null) {
            throw new NullPointerException(CLASS_CANNOT_BE_NULL);
        }

        CameraModification mod = this.cameraModificationMap.get(cls);

        if (mod == null) {
            throw new IllegalStateException("camera modification is not registered: " + cls.getName());
        }

        if (this.activeModifications.contains(mod)) {
            this.activeModifications.remove(mod);
        }
    }

    /**
    * get original camera, only for junit tests and tiled map renderer
     *
     * @return original camera
    */
    public OrthographicCamera getOriginalCamera () {
        return this.camera;
    }

    public int countActiveMods() {
        return this.activeModifications.size();
    }

    @Override
    public String toString () {
        return "CameraHelper (x: " + this.x + ", y: " + this.y + ", width: " + this.width + ", height: " + this.height + ", bounds: {minX: " + minX + ", maxX: " + maxX + ", minY: " + minY + ", maxY: " + maxY + "})";
    }

}
