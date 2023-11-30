package code.with.bingyang.wei.hogwartsartifactsonline.system.exception;

public class ObjectNotFoundException extends RuntimeException {

    public ObjectNotFoundException(String objectName, String id) {
        super("Could not find " + objectName + " with Id " + id + " :(");
    }

    public ObjectNotFoundException(String objectName, int id) {
        super("Could not find " + objectName + " with Id " + id + " :(");
    }

}
