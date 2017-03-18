package dao;

import java.io.Serializable;

/**
 * Created by zhao on 2017/2/28.
 */

public class Pool   implements Serializable{
    private String poolid;
    private String baseid;
    private String baseName;
    private String userName;

    public String getBaseid() {
        return baseid;
    }

    public String getBaseName() {
        return baseName;
    }

    public String getPoolid() {
        return poolid;
    }

    public String getUserName() {
        return userName;
    }

    public void setBaseid(String baseid) {
        this.baseid = baseid;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public void setPoolid(String poolid) {
        this.poolid = poolid;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


}
