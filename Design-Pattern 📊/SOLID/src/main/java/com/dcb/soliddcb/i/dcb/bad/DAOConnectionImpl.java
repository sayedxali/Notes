package com.dcb.soliddcb.i.dcb.bad;

public class DAOConnectionImpl implements BadDAOInterface {

    @Override
    public void openConnection() {
        // This is fine; since we're in a db class
    }

    @Override
    public void createRecord() {
        // This is fine; since we're in a db class
    }

    @Override
    public void openFile() {
        // We are in DB Connection so no need to support open file
        throw new UnsupportedOperationException("Open file Not supported");
    }

    @Override
    public void deleteRecord() {
        // This is fine; since we're in a db class
    }

}
