package ru.expensive.common.util.render;

import ru.expensive.common.QuickImports;

import static org.lwjgl.opengl.EXTFramebufferObject.*;
import static org.lwjgl.opengl.EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT;
import static org.lwjgl.opengl.GL11.*;

public class Stencil implements QuickImports {

    public static void push() {
        var framebuffer = mc.getFramebuffer();
        if (framebuffer.depthAttachment > -1) {
            mc.getFramebuffer().beginWrite(false);

            glDeleteRenderbuffersEXT(framebuffer.depthAttachment);
            final int stencilDepthBufferID = glGenRenderbuffersEXT();
            glBindRenderbufferEXT(GL_RENDERBUFFER_EXT, stencilDepthBufferID);
            glRenderbufferStorageEXT(GL_RENDERBUFFER_EXT, GL_DEPTH_STENCIL_EXT, window.getWidth(), window.getHeight());
            glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_STENCIL_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT,
                    stencilDepthBufferID);
            glFramebufferRenderbufferEXT(GL_FRAMEBUFFER_EXT, GL_DEPTH_ATTACHMENT_EXT, GL_RENDERBUFFER_EXT,
                    stencilDepthBufferID);
            framebuffer.depthAttachment = -1;
        }

        glStencilMask(0xFF);
        glClear(GL_STENCIL_BUFFER_BIT);
        glEnable(GL_STENCIL_TEST);
        glStencilFunc(GL_ALWAYS, 1, 1);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glDisable(GL_DEPTH_TEST);
        glColorMask(false, false, false, false);
    }

    public static void read(int ref) {
        glColorMask(true, true, true, true);
        glStencilFunc(GL_EQUAL, ref, 1);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
    }

    public static void pop() {
        glDisable(GL_STENCIL_TEST);
        glEnable(GL_DEPTH_TEST);
    }
}