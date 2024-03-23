package com.dcb.soliddcb.i.dcb.bad;

public class FileDAOConnectionImpl implements BadDAOInterface {

    @Override
    public void openConnection() {
        //We can't open connection in file system
        throw new UnsupportedOperationException("Open db Not supported in this class.");
    }

    @Override
    public void createRecord() {

    }

    @Override
    public void openFile() {

    }

    @Override
    public void deleteRecord() {

    }

}
