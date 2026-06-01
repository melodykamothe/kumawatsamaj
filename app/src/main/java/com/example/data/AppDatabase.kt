package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [MemberEntity::class], version = 6, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun memberDao(): MemberDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "kumawat_samaaj_db"
                )
                .fallbackToDestructiveMigration()
                .addCallback(AppDatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class AppDatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            // Prepopulate using direct, synchronous SQL inserts so it happens safely in the creation transaction
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Rajesh Kumawat', '9820112345', 'avatar_1', 'navi_mumbai1', 'Advisor')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Suresh Kumawat', '9820223456', 'avatar_2', 'navi_mumbai1', 'Secretary')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Sunita Kumawat', '9820334567', 'avatar_3', 'navi_mumbai1', 'Treasurer')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Ramesh Chand Kumawat', '9820445678', 'avatar_4', 'navi_mumbai1', 'President')")

            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Mahendra Kumawat', '9930156789', 'avatar_5', 'mumbai1', 'President')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Kailash Kumawat', '9930267890', 'avatar_1', 'mumbai1', 'Advisor')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Nirmala Kumawat', '9930378901', 'avatar_2', 'mumbai1', 'Member')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Lalita Kumawat', '9930489012', 'avatar_3', 'mumbai1', 'Member')")

            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HEMTARAM SONARAMJI PRAJAPAT', '9414588156', 'avatar_1', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('AMRUTLAL POLARAMJI', '9950904885', 'avatar_2', 'ahore1', 'GUDA BALOTAN')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('GOPARAM KHANGARARAMJI', '9414588252', 'avatar_3', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HARISH KUMAR MULAJI', '9324635502', 'avatar_4', 'ahore1', 'AGAWARI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('ASHOK KUMAR KESAJI', '9414534995', 'avatar_5', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RAMESH KUMAR VAGARAMJI', '9849220626', 'avatar_1', 'ahore1', 'GUDA BALOTAN')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SHRAVAN KUMAR MITHALALAJI', '9772885985', 'avatar_2', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('DINESH KUMAR SARUPAJI', '9950759808', 'avatar_3', 'ahore1', 'CHANDRAI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHOMARAM UMARAMJI', '9982730398', 'avatar_4', 'ahore1', 'CHARLI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('JAYANTILAL JETHARAMJI', '7014392799', 'avatar_5', 'ahore1', 'BAAVDI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MOHANLAL CHUNNILALJI', '9414867831', 'avatar_1', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('JHAKMARAM PRABHUJI', '9414534248', 'avatar_2', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('INDRA KUMAR HEMARAMJI', '8107597561', 'avatar_3', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PANCHARAM TARARAMJI', '8769657272', 'avatar_4', 'ahore1', 'PANCHOTA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BIJESH KUMAR HANSARAJJI', '8094638347', 'avatar_5', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HANSARAM KESARAMJI', '9672847384', 'avatar_1', 'ahore1', 'JHODA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('AMRUTLAL NARSARAMJI', '9772817402', 'avatar_2', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KANHAILAL ACHLARAMJI', '9982744639', 'avatar_3', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HANU PRAJAPATI KUYARAMJI', '8742038838', 'avatar_4', 'ahore1', 'MAADADI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('DINESH KUMAR JAWANARAMJI', '9829919199', 'avatar_5', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HANSARAM MANGILALJI', '9772974699', 'avatar_1', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('GANESHARAM KESARAMJI', '9783140265', 'avatar_2', 'ahore1', 'BHOOTI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RAMESH KUMAR THANARAMJI', '9825555938', 'avatar_3', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('VAGARAM ACHLARAMJI', '9828373207', 'avatar_4', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KULDEEP RATARAMJI', '9604100776', 'avatar_5', 'ahore1', 'BHOOTI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PHULARAM MODARAMJI', '9107460987', 'avatar_1', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KISHOREKUMAR RUPARAMJI', '6377219606', 'avatar_2', 'ahore1', 'CHUNDA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RANCHODARAM AMRARAMJI', '9414588348', 'avatar_3', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('JAYANTILAL SHANKARALALJI', '9983794508', 'avatar_4', 'ahore1', 'JHODA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HEMARAM CHUNNILALJI', '9929476221', 'avatar_5', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('VAGARAM BHABUTARAMJI', '9828070568', 'avatar_1', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MOHANLAL PURARAMJI', '9828406889', 'avatar_2', 'ahore1', 'GUDA INDRA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SOMARAM CHUNNILALJI', '9982915510', 'avatar_3', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PAARAS KUMAR MITHALALJI', '8875560710', 'avatar_4', 'ahore1', 'SUGALIYA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('JAWANARAM HABTAJI', '9414588665', 'avatar_5', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BABULAL HOSAJI', '9322831477', 'avatar_1', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHIMARAM MULAJI', '9602312014', 'avatar_2', 'ahore1', 'CHAVARCA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MANGILAL BHALARAMJI', '8107712152', 'avatar_3', 'ahore1', 'CHARLI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SUJARAM UMARAMJI', '6350453200', 'avatar_4', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RAMARAM KANARAMJI', '9549968614', 'avatar_5', 'ahore1', 'BHOOTI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MOHANLAL KHUSHALJI', '9004466921', 'avatar_1', 'ahore1', 'CHANDRAI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHAWARLAL SHRIPRABHUJI', '9460093599', 'avatar_2', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('CHAMPALAL SHRI RAJAAJI', '9929850793', 'avatar_3', 'ahore1', 'BHOOTI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHIKHARAM MAGAAJI', '9829216556', 'avatar_4', 'ahore1', 'RODLA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SAKARAM RANCHODJI', '9829516941', 'avatar_5', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHABUTARAM NEMAJI', '9610288352', 'avatar_1', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MITHALAL KUPAAJI', '9414534350', 'avatar_2', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('GANESHARAM KHETAJI', '9672913254', 'avatar_3', 'ahore1', 'JORA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KHANGARARAM VAAGAJI', '9610033276', 'avatar_4', 'ahore1', 'GUDA BALOTAN')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MADARAM JAWAANJI', '9448705827', 'avatar_5', 'ahore1', 'JORA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KANTILAL VARDAJI', '9549240582', 'avatar_1', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MANGILAL VARDAJI', '9413266718', 'avatar_2', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BADARAM KUPAJI', '9460712853', 'avatar_3', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('FUTARMAL JESAJI', '9828930245', 'avatar_4', 'ahore1', 'UMMEDPUR')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PRAVIN KUMAR DEVAJI', '9636941117', 'avatar_5', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SHANKARLAL SANKLAJI', '9892308093', 'avatar_1', 'ahore1', 'JORA')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MANISH KUMAR PUKHRAJJI', '8949084149', 'avatar_2', 'ahore1', 'UMMEDPUR')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('DILIP KUMAR MANGILALJI', '7527026926', 'avatar_3', 'ahore1', 'CHARLI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MANOJ KUMAR MANGILALJI', '9998537113', 'avatar_4', 'ahore1', 'CHARLI')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SAWLARAM MODAJI', '7413910024', 'avatar_5', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SHANTILAL VAGTAJI', '9983184728', 'avatar_1', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KAILASH KUMAR LUMBAJI', '9982209858', 'avatar_2', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PRADIP KUMAR RANCHODAJI', '9100591722', 'avatar_3', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('VINOD KUMAR KUYAJI', '9783860219', 'avatar_4', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SHRAWAN KUMAR SHESHAJI', '9414544883', 'avatar_5', 'ahore1', 'AHORE')")
            db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RAMESH KUMAR MAGAJI', '9448366427', 'avatar_1', 'ahore1', 'AHORE')")
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            scope.launch(Dispatchers.IO) {
                var count = 0
                try {
                    count = db.compileStatement("SELECT COUNT(*) FROM members").simpleQueryForLong().toInt()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (count == 0) {
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Rajesh Kumawat', '9820112345', 'avatar_1', 'navi_mumbai1', 'Advisor')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Suresh Kumawat', '9820223456', 'avatar_2', 'navi_mumbai1', 'Secretary')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Sunita Kumawat', '9820334567', 'avatar_3', 'navi_mumbai1', 'Treasurer')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Ramesh Chand Kumawat', '9820445678', 'avatar_4', 'navi_mumbai1', 'President')")

                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Mahendra Kumawat', '9930156789', 'avatar_5', 'mumbai1', 'President')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Kailash Kumawat', '9930267890', 'avatar_1', 'mumbai1', 'Advisor')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Nirmala Kumawat', '9930378901', 'avatar_2', 'mumbai1', 'Member')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('Lalita Kumawat', '9930489012', 'avatar_3', 'mumbai1', 'Member')")

                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HEMTARAM SONARAMJI PRAJAPAT', '9414588156', 'avatar_1', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('AMRUTLAL POLARAMJI', '9950904885', 'avatar_2', 'ahore1', 'GUDA BALOTAN')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('GOPARAM KHANGARARAMJI', '9414588252', 'avatar_3', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HARISH KUMAR MULAJI', '9324635502', 'avatar_4', 'ahore1', 'AGAWARI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('ASHOK KUMAR KESAJI', '9414534995', 'avatar_5', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RAMESH KUMAR VAGARAMJI', '9849220626', 'avatar_1', 'ahore1', 'GUDA BALOTAN')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SHRAVAN KUMAR MITHALALAJI', '9772885985', 'avatar_2', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('DINESH KUMAR SARUPAJI', '9950759808', 'avatar_3', 'ahore1', 'CHANDRAI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHOMARAM UMARAMJI', '9982730398', 'avatar_4', 'ahore1', 'CHARLI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('JAYANTILAL JETHARAMJI', '7014392799', 'avatar_5', 'ahore1', 'BAAVDI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MOHANLAL CHUNNILALJI', '9414867831', 'avatar_1', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('JHAKMARAM PRABHUJI', '9414534248', 'avatar_2', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('INDRA KUMAR HEMARAMJI', '8107597561', 'avatar_3', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PANCHARAM TARARAMJI', '8769657272', 'avatar_4', 'ahore1', 'PANCHOTA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BIJESH KUMAR HANSARAJJI', '8094638347', 'avatar_5', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HANSARAM KESARAMJI', '9672847384', 'avatar_1', 'ahore1', 'JHODA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('AMRUTLAL NARSARAMJI', '9772817402', 'avatar_2', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KANHAILAL ACHLARAMJI', '9982744639', 'avatar_3', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HANU PRAJAPATI KUYARAMJI', '8742038838', 'avatar_4', 'ahore1', 'MAADADI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('DINESH KUMAR JAWANARAMJI', '9829919199', 'avatar_5', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HANSARAM MANGILALJI', '9772974699', 'avatar_1', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('GANESHARAM KESARAMJI', '9783140265', 'avatar_2', 'ahore1', 'BHOOTI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RAMESH KUMAR THANARAMJI', '9825555938', 'avatar_3', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('VAGARAM ACHLARAMJI', '9828373207', 'avatar_4', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KULDEEP RATARAMJI', '9604100776', 'avatar_5', 'ahore1', 'BHOOTI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PHULARAM MODARAMJI', '9107460987', 'avatar_1', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KISHOREKUMAR RUPARAMJI', '6377219606', 'avatar_2', 'ahore1', 'CHUNDA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RANCHODARAM AMRARAMJI', '9414588348', 'avatar_3', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('JAYANTILAL SHANKARALALJI', '9983794508', 'avatar_4', 'ahore1', 'JHODA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('HEMARAM CHUNNILALJI', '9929476221', 'avatar_5', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('VAGARAM BHABUTARAMJI', '9828070568', 'avatar_1', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MOHANLAL PURARAMJI', '9828406889', 'avatar_2', 'ahore1', 'GUDA INDRA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SOMARAM CHUNNILALJI', '9982915510', 'avatar_3', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PAARAS KUMAR MITHALALJI', '8875560710', 'avatar_4', 'ahore1', 'SUGALIYA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('JAWANARAM HABTAJI', '9414588665', 'avatar_5', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BABULAL HOSAJI', '9322831477', 'avatar_1', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHIMARAM MULAJI', '9602312014', 'avatar_2', 'ahore1', 'CHAVARCA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MANGILAL BHALARAMJI', '8107712152', 'avatar_3', 'ahore1', 'CHARLI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SUJARAM UMARAMJI', '6350453200', 'avatar_4', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RAMARAM KANARAMJI', '9549968614', 'avatar_5', 'ahore1', 'BHOOTI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MOHANLAL KHUSHALJI', '9004466921', 'avatar_1', 'ahore1', 'CHANDRAI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHAWARLAL SHRIPRABHUJI', '9460093599', 'avatar_2', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('CHAMPALAL SHRI RAJAAJI', '9929850793', 'avatar_3', 'ahore1', 'BHOOTI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHIKHARAM MAGAAJI', '9829216556', 'avatar_4', 'ahore1', 'RODLA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SAKARAM RANCHODJI', '9829516941', 'avatar_5', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BHABUTARAM NEMAJI', '9610288352', 'avatar_1', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MITHALAL KUPAAJI', '9414534350', 'avatar_2', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('GANESHARAM KHETAJI', '9672913254', 'avatar_3', 'ahore1', 'JORA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KHANGARARAM VAAGAJI', '9610033276', 'avatar_4', 'ahore1', 'GUDA BALOTAN')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MADARAM JAWAANJI', '9448705827', 'avatar_5', 'ahore1', 'JORA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KANTILAL VARDAJI', '9549240582', 'avatar_1', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MANGILAL VARDAJI', '9413266718', 'avatar_2', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('BADARAM KUPAJI', '9460712853', 'avatar_3', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('FUTARMAL JESAJI', '9828930245', 'avatar_4', 'ahore1', 'UMMEDPUR')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PRAVIN KUMAR DEVAJI', '9636941117', 'avatar_5', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SHANKARLAL SANKLAJI', '9892308093', 'avatar_1', 'ahore1', 'JORA')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MANISH KUMAR PUKHRAJJI', '8949084149', 'avatar_2', 'ahore1', 'UMMEDPUR')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('DILIP KUMAR MANGILALJI', '7527026926', 'avatar_3', 'ahore1', 'CHARLI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('MANOJ KUMAR MANGILALJI', '9998537113', 'avatar_4', 'ahore1', 'CHARLI')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SAWLARAM MODAJI', '7413910024', 'avatar_5', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SHANTILAL VAGTAJI', '9983184728', 'avatar_1', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('KAILASH KUMAR LUMBAJI', '9982209858', 'avatar_2', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('PRADIP KUMAR RANCHODAJI', '9100591722', 'avatar_3', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('VINOD KUMAR KUYAJI', '9783860219', 'avatar_4', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('SHRAWAN KUMAR SHESHAJI', '9414544883', 'avatar_5', 'ahore1', 'AHORE')")
                    db.execSQL("INSERT INTO members (name, contactNumber, profilePhotoUri, subCommunity, position) VALUES ('RAMESH KUMAR MAGAJI', '9448366427', 'avatar_1', 'ahore1', 'AHORE')")
                }
            }
        }
    }
}
