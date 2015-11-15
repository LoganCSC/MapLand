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

import com.barrybecker4.mapland.screens.LandGrabMapActivity;
import com.barrybecker4.mapland.screens.GameManagementActivity;


/**
 * A list of all the demos we have available.
 */
public final class ScreenDetailsList {

    /** This class should not be instantiated. */
    private ScreenDetailsList() {}

    public static final ScreenDetails[] DEMOS = {
        new ScreenDetails(R.string.map_land_demo_label,
                        R.string.land_grab_description,
                        LandGrabMapActivity.class),
        new ScreenDetails(R.string.game_management_label,
                        R.string.game_management_description,
                        GameManagementActivity.class),
    };
}
