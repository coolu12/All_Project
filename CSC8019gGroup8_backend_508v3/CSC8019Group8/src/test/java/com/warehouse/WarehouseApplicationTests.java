package com.warehouse;

import com.warehouse.dao.StorageDao;
import com.warehouse.pojo.Storage;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class WarehouseApplicationTests {
    @Autowired
    StorageDao storageDao;
    @Test
    public void testGetStorageList(){
        List<Storage> storageList = storageDao.getStorageList(null,null,null,
                null,null,null,null,null,null);
        for (Storage storage : storageList) {
            System.out.println(storage);
        }
    }



}
