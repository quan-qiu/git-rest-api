package com.vtyc;

import com.vtyc.HelloWorld;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class MyApplication extends Application{

    @Override
    public Set<Class<?>> getClasses(){
        HashSet h = new HashSet<Class<?>>();
        h.add( HelloWorld.class );
        h.add(UserCard.class);
        h.add(HrAuthentication.class);
        h.add(HrQuery.class);
        return h;

    }
}
