package ammar.samar.developtask.models;

import com.google.gson.JsonObject;

import java.util.List;

public class ReposModel {

    public String repoName;
     public String repoDescription;
    public String OwnerName;
    public String OwnerUrl;
    public boolean fork;
    public String repoUrl;



    public String getRepoName() {
        return repoName;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public String getRepoDescription() {
        return repoDescription;
    }

    public void setRepoDescription(String repoDescription) {
        this.repoDescription = repoDescription;
    }

    public String getOwnerName() {
        return OwnerName;
    }

    public void setOwnerName(String ownerName) {
        OwnerName = ownerName;
    }

    public String getOwnerUrl() {
        return OwnerUrl;
    }

    public void setOwnerUrl(String ownerUrl) {
        OwnerUrl = ownerUrl;
    }

    public boolean isFork() {
        return fork;
    }

    public void setFork(boolean fork) {
        this.fork = fork;
    }

    public String getRepoUrl() {
        return repoUrl;
    }

    public void setRepoUrl(String repoUrl) {
        this.repoUrl = repoUrl;
    }
}
