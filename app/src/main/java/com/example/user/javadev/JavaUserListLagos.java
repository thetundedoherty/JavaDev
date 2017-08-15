package com.example.user.javadev;

/**
 * Created by TheTundeDoherty on 7/16/2017.
 */

public class JavaUserListLagos {
    //Name of the user
    private String mLogin;

    //Url to the profile of the user
    private String mProfileUrl;

    //Url to the profile picture of the user
    private String mAvatarUrl;

    //id of the user
    private int mId;

    //Create a new object JavaUserListLagos with the following parameters ProfileName,
    //avatarUrl, profileUrl, id.
    /**
     *
     * @param profileName
     * @param avatarUrl
     * @param profileUrl
     * @param id
     */

    public JavaUserListLagos(String profileName, String avatarUrl, String profileUrl, int id) {
        this.mLogin = profileName;
        this.mAvatarUrl = avatarUrl;
        this.mProfileUrl = profileUrl;
        this.mId = id;
    }

    //Get the login as profile name
    public String getmLogin(){
       return mLogin;
   }

   //Get the profile url
   public String getmProfileUrl(){
       return mProfileUrl;
   }

   //Get the avatar url
   public String getmAvatarUrl(){
       return mAvatarUrl;
   }

   //Get the id of the user
   public int getmId(){
       return mId;
   }

}
