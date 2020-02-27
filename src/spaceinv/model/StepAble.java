package spaceinv.model;

/*
   Must be implemented by anything that can be positioned in the world.
   Used by GUI. This must be fulfilled by any object the GUI shall render
 */
public interface StepAble {
    void step();
}
