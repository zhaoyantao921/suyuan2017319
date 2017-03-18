package dao;

import java.io.Serializable;

/**
 * Created by zhao on 2017/3/1.
 *
 * http://202.121.66.53:8080/MySpringMybatis/typeInfoByPoolidApi?poolid=1
 * [{"InTime":"2016-08-23","poolid":"1","typeName":"鳜鱼","typeid":"2"},{"InTime":"2016-08-16","poolid":"1","typeid":"33"}]
 */

public class Breed  implements Serializable {
    private String poolid;
    private String InTime;
    private String typeName;
    private String typeid;

    public String getPoolid() {
        return poolid;
    }

    public void setPoolid(String poolid) {
        this.poolid = poolid;
    }

    public String getInTime() {
        return InTime;
    }

    public void setInTime(String inTime) {
        InTime = inTime;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }
}
