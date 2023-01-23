/*
 * Fork Engine 2D
 * Copyright (C) 2023 XenFork Union
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package union.xenfork.fe2d

typealias ResizeFun = ((width: Int, height: Int, base: (width: Int, height: Int) -> Unit) -> Unit)?
typealias CursorPosFun = ((posX: Double, posY: Double, base: (posX: Double, posY: Double) -> Unit) -> Unit)?
typealias KeyFun = ((key: Int, scancode: Int, action: Input.Action, mods: Int, base: (key: Int, scancode: Int, action: Input.Action, mods: Int) -> Unit) -> Unit)?
typealias MouseButtonFun = ((button: Int, action: Input.Action, mods: Int, base: (button: Int, action: Input.Action, mods: Int) -> Unit) -> Unit)?

typealias StartFun = ((base: () -> Unit) -> Unit)?
typealias InitFun = ((base: () -> Unit) -> Unit)?

typealias FixedUpdateFun = ((base: () -> Unit) -> Unit)?
typealias UpdateFun = ((base: () -> Unit) -> Unit)?
typealias LateUpdateFun = ((base: () -> Unit) -> Unit)?

typealias RenderFun = ((delta: Double, base: (delta: Double) -> Unit) -> Unit)?

typealias DisposeFun = ((base: () -> Unit) -> Unit)?

typealias KeyPressFun = ((key: Int, scancode: Int, mods: Int, base: (key: Int, scancode: Int, mods: Int) -> Unit) -> Unit)?
typealias MousePressFun = ((button: Int, mods: Int, base: (button: Int, mods: Int) -> Unit) -> Unit)?

/**
 * The kotlin game builder.
 *
 * @author squid233
 * @since 0.1.0
 */
class KtGame : Game() {
    private var resizeFun: ResizeFun = null
    private var cursorPosFun: CursorPosFun = null
    private var keyFun: KeyFun = null
    private var mouseButtonFun: MouseButtonFun = null
    private var startFun: StartFun = null
    private var initFun: InitFun = null
    private var fixedUpdateFun: FixedUpdateFun = null
    private var updateFun: UpdateFun = null
    private var lateUpdateFun: LateUpdateFun = null
    private var renderFun: RenderFun = null
    private var disposeFun: DisposeFun = null
    private var keyPressFun: KeyPressFun = null
    private var mousePressFun: MousePressFun = null

    public override fun log(useStderr: Boolean, msg: String?) {
        super.log(useStderr, msg)
    }

    fun onResize(block: ResizeFun) {
        resizeFun = block
    }

    fun onCursorPos(block: CursorPosFun) {
        cursorPosFun = block
    }

    fun onKey(block: KeyFun) {
        keyFun = block
    }

    fun onMouseButton(block: MouseButtonFun) {
        mouseButtonFun = block
    }

    fun onStart(block: StartFun) {
        startFun = block
    }

    fun onInit(block: InitFun) {
        initFun = block
    }

    fun onFixedUpdate(block: FixedUpdateFun) {
        fixedUpdateFun = block
    }

    fun onUpdate(block: UpdateFun) {
        updateFun = block
    }

    fun onLateUpdate(block: LateUpdateFun) {
        lateUpdateFun = block
    }

    fun onRender(block: RenderFun) {
        renderFun = block
    }

    fun onDispose(block: DisposeFun) {
        disposeFun = block
    }

    fun onKeyPress(block: KeyPressFun) {
        keyPressFun = block
    }

    fun onMousePress(block: MousePressFun) {
        mousePressFun = block
    }

    override fun onResize(width: Int, height: Int) = when (resizeFun) {
        null -> super.onResize(width, height)
        else -> resizeFun!!(width, height) { width1, height1 -> super.onResize(width1, height1) }
    }

    override fun onCursorPos(posX: Double, posY: Double) = when (cursorPosFun) {
        null -> super.onCursorPos(posX, posY)
        else -> cursorPosFun!!(posX, posY) { posX1, posY1 -> super.onCursorPos(posX1, posY1) }
    }

    override fun onKey(key: Int, scancode: Int, action: Input.Action, mods: Int) = when (keyFun) {
        null -> super.onKey(key, scancode, action, mods)
        else -> keyFun!!(key, scancode, action, mods) { key1, scancode1, action1, mods1 ->
            super.onKey(
                key1,
                scancode1,
                action1,
                mods1
            )
        }
    }

    override fun onMouseButton(button: Int, action: Input.Action, mods: Int) = when (mouseButtonFun) {
        null -> super.onMouseButton(button, action, mods)
        else -> mouseButtonFun!!(button, action, mods) { button1, action1, mods1 ->
            super.onMouseButton(
                button1,
                action1,
                mods1
            )
        }
    }

    override fun start() = when (startFun) {
        null -> super.start()
        else -> startFun!!{ super.start() }
    }

    override fun init() = when (initFun) {
        null -> super.init()
        else -> initFun!!{ super.init() }
    }

    override fun fixedUpdate() = when (fixedUpdateFun) {
        null -> super.fixedUpdate()
        else -> fixedUpdateFun!!{ super.fixedUpdate() }
    }

    override fun update() = when (updateFun) {
        null -> super.update()
        else -> updateFun!!{ super.update() }
    }

    override fun lateUpdate() = when (lateUpdateFun) {
        null -> super.lateUpdate()
        else -> lateUpdateFun!!{ super.lateUpdate() }
    }

    override fun render(delta: Double) = when (renderFun) {
        null -> super.render(delta)
        else -> renderFun!!(delta) { delta1 -> super.render(delta1) }
    }

    override fun dispose() = when (disposeFun) {
        null -> super.dispose()
        else -> disposeFun!!{ super.dispose() }
    }

    override fun onKeyPress(key: Int, scancode: Int, mods: Int) = when (keyPressFun) {
        null -> super.onKeyPress(key, scancode, mods)
        else -> keyPressFun!!(key, scancode, mods) { key1, scancode1, mods1 ->
            super.onKeyPress(
                key1,
                scancode1,
                mods1
            )
        }
    }

    override fun onMousePress(button: Int, mods: Int) = when (mousePressFun) {
        null -> super.onMousePress(button, mods)
        else -> mousePressFun!!(button, mods) { button1, mods1 -> super.onMousePress(button1, mods1) }
    }
}

/**
 * Creates and launches a game with the given configuration.
 *
 * @see ApplicationConfig
 */
fun game(
    useStderr: Boolean = false,
    applicationName: String = "Fork Engine 2D Game",
    windowWidth: Int = 800,
    windowHeight: Int = 600,
    windowTitle: String? = null,
    vsync: Boolean = true,
    resizable: Boolean = true,
    block: KtGame.() -> Unit
) {
    KtGame().apply {
        block()
        launch(ApplicationConfig().also {
            it.useStderr = useStderr
            it.applicationName = applicationName
            it.windowWidth = windowWidth
            it.windowHeight = windowHeight
            it.windowTitle = windowTitle
            it.vsync = vsync
            it.resizable = resizable
        })
    }
}
