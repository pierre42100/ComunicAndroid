package org.communiquons.android.comunic.client.data.UsersInfo;

/**
 * This class contains the informations about a single user
 *
 * @author Pierre HUBERT
 * Created by pierre on 11/2/17.
 */

public class UserInfo {

    /**
     * Informations about the user
     */
    private int id;
    private String firstName;
    private String lastName;
    private String accountImageURL;

    /**
     * Set the ID of the user
     *
     * @param id The ID to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the ID of the user
     *
     * @return The ID of the user
     */
    public int getId() {
        return id;
    }

    /***
     * Set the first name of the user
     *
     * @param firstName The new first name of the user
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Get the first name of the user
     *
     * @return The first name of the user
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Set the last name of the user
     *
     * @param lastName The last name of the user
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Get the last name of the user
     *
     * @return The last name of the user
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Get the full name of the user
     *
     * @return The full name of the user
     */
    public String getFullName(){
        return firstName + " " + lastName;
    }

    /**
     * Set the image URL of the account of the user
     *
     * @param imageURL The URL of the image
     */
    public void setAccountImageURL(String imageURL) {
        this.accountImageURL = imageURL;
    }

    /**
     * Get the image URL of the account of the user
     *
     * @return The image URL of the account of the user
     */
    public String getAcountImageURL() {
        return accountImageURL;
    }
}
