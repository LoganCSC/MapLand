package com.barrybecker4.mapland.server;

import com.google.api.client.json.GenericJson;

/**
 * @author Barry Becker
 */
public interface IRetrievalHandler {

    public void entityRetrieved(GenericJson entity);
}
