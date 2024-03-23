package com.dcb.soliddcb.i.dcb.bad;

/**
 * This dao interface defined to support data access using file system
 * or database system.
 * <p>
 * So we have added openConnection and openFile for it
 * this bad because we have accommodated all operation in single interface.
 */
public interface BadDAOInterface {

    void openConnection(); // db operation as well

    void createRecord();

    void openFile(); // file operation as well

    void deleteRecord();

}