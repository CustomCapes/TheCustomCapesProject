/*
 *  This file is part of the CustomCapes Project (https://github.com/CustomCapes)
 *  Copyright (C) 2022  ccetl
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ccetl.customcapes.client;

import net.fabricmc.api.ClientModInitializer;

@net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
public class CustomCapesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        util.INSTANCE.checkApiStatus();
        util.INSTANCE.checkConnection();
        util.INSTANCE.createFolders();
    }

}
