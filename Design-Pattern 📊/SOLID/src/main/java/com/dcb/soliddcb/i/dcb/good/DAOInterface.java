package com.dcb.soliddcb.i.dcb.good;

/**
 * This is good we will only include the dao operation
 * And segregate connection part so consumer can implement required interfaces.
 */
public interface DAOInterface {

    void createRecord();

    void deleteRecord();

}
