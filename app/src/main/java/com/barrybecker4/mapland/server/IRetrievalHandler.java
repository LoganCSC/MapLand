package com.barrybecker4.mapland.server;

import com.google.api.client.json.GenericJson;

/**
 * Created by becker on 10/10/2015.
 */
public interface IRetrievalHandler {

    public void entityRetrieved(GenericJson entity);
}
