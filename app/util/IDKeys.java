package util;

import controllers.MongoDBController;
import models.ID;
import org.joda.time.DateTime;
import org.jongo.MongoCollection;

/**
 * @Author Murali
 */
public class IDKeys {

    public static String customer = "US";


    public static String generateUniqueId (String type){

        MongoCollection idSeed = MongoDBController.getCollection(CollectionNames.idcollection);
        ID id = idSeed.findOne().as(ID.class);

        int uniqueNumber = id.val.intValue();
        id.val = new Integer(uniqueNumber +1);
        idSeed.update("{val: #}", uniqueNumber).upsert().with("{val : #}", (uniqueNumber +1));
        //idSeed.update().with("{val : " + id.val + "}");

        DateTime now = DateTime.now();
        String uniqueId = now.year().getAsString() + now.monthOfYear().getAsString()  +type + uniqueNumber;

        return uniqueId;
    }
}
