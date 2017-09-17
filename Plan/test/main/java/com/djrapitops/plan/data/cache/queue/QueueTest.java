/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main.java.com.djrapitops.plan.data.cache.queue;

import main.java.com.djrapitops.plan.Plan;
import main.java.com.djrapitops.plan.database.Database;
import main.java.com.djrapitops.plan.database.databases.SQLiteDB;
import main.java.com.djrapitops.plan.systems.cache.DataCache;
import main.java.com.djrapitops.plan.utilities.MiscUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import test.java.utils.TestInit;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.powermock.api.mockito.PowerMockito.when;

// TODO Rewrite
@RunWith(PowerMockRunner.class)
@PrepareForTest({JavaPlugin.class})
public class QueueTest {

    private Database db;

    @Before
    public void setUp() throws Exception {
        TestInit t = TestInit.init();
        Plan plan = t.getPlanMock();

        db = new SQLiteDB(plan, "debug" + MiscUtils.getTime());
        db.init();
        when(plan.getDB()).thenReturn(db);

        DataCache dataCache = new DataCache(plan);
        when(plan.getDataCache()).thenReturn(dataCache);
    }

    @After
    public void tearDown() throws SQLException {
        db.close();
    }

    @Test
    public void testProcessQueue() {
        List<Integer> processCalls = new ArrayList<>();
        List<Integer> errors = new ArrayList<>();
        // TODO Rewrite
    }
}
