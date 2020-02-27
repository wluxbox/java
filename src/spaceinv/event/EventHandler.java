
package spaceinv.event;

/*
    Must be implemented by all classes acting as targets
    for events from model.

    *** Nothing to do here ***
 */
public interface EventHandler {
    void onModelEvent(ModelEvent evt);
}
