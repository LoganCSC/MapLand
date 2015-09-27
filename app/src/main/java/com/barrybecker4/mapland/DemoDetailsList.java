/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.barrybecker4.mapland;

import com.barrybecker4.mapland.screens.BasicMapDemoActivity;
import com.barrybecker4.mapland.screens.EventsDemoActivity;
import com.barrybecker4.mapland.screens.PolygonDemoActivity;
import com.barrybecker4.mapland.screens.TileCoordinateDemoActivity;

/**
 * A list of all the demos we have available.
 */
public final class DemoDetailsList {

    /** This class should not be instantiated. */
    private DemoDetailsList() {}

    public static final DemoDetails[] DEMOS = {
        new DemoDetails(R.string.basic_map_demo_label,
                        R.string.basic_map_demo_description,
                        BasicMapDemoActivity.class),
        new DemoDetails(R.string.events_demo_label,
                        R.string.events_demo_description,
                        EventsDemoActivity.class),
        new DemoDetails(R.string.polygon_demo_label,
                        R.string.polygon_demo_description,
                        PolygonDemoActivity.class),
        new DemoDetails(R.string.tile_coordinate_demo_label,
                        R.string.tile_coordinate_demo_description,
                        TileCoordinateDemoActivity.class),
    };
}
