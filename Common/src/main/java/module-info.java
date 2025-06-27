module fr.butinfoalt.riseandfall {
    requires java.sql;
    exports fr.butinfoalt.riseandfall.util.counter;
    exports fr.butinfoalt.riseandfall.util.function;
    exports fr.butinfoalt.riseandfall.util.logging;
    exports fr.butinfoalt.riseandfall.util;
    exports fr.butinfoalt.riseandfall.gamelogic;
    exports fr.butinfoalt.riseandfall.network.client;
    exports fr.butinfoalt.riseandfall.network.common;
    exports fr.butinfoalt.riseandfall.network.packets;
    exports fr.butinfoalt.riseandfall.network.packets.data;
    exports fr.butinfoalt.riseandfall.network.server;
    exports fr.butinfoalt.riseandfall.gamelogic.data;
}
