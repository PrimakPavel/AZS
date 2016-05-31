package su.moy.chernihov.mapapplication;


import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.FirebaseException;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FireBaseConnections {
    // адрес моей БД
    private static final String REF = "https://fiery-torch-4484.firebaseio.com/";
    private static final String REF_RATING_ALL = "https://fiery-torch-4484.firebaseio.com/ratingAll/";
    private static final String REF_DOWNLOAD_ALL = "https://fiery-torch-4484.firebaseio.com/azsAll/";
   /* // ключи в БД
    private static final String AUTHOR = "author";
    private static final String MESSAGE = "message";
    private static final String RATING = "rating";
    private static final String DATE = "date";*/

    private static FireBaseConnections outInstance = new FireBaseConnections();
    private static Context mContext;

    private FireBaseConnections() {
    }

    // статический метод для создания объекта FireBaseConnections
    public static FireBaseConnections getInstance(Context context) {
        mContext = context;
        // передаю контекс в Firebase (согласно мануалу) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Firebase.setAndroidContext(context);
        // вход по логину и паролю
        //authentication(new Firebase(REF));
        return outInstance;
    }
    public String write(String id, String authorName, String message, String rating, String date) {
        // для каждой заправки создаю свою ветку постов
        Firebase ref = new Firebase(REF_RATING_ALL);


        Firebase postRef = ref.child("post" + id);
        // для каждого нового сообщения будет сгенерирован свой ID
        Firebase newPostRef = postRef.push();
        // сообщение
        /*Map<String, String> myPost = new HashMap<>();
        // автор сообщения
        myPost.put(AUTHOR, authorName);
        // текст сообщения
        myPost.put(MESSAGE, message);
        // текущая оценка заправки
        myPost.put(RATING, rating);
        // дата записи сообщения
        myPost.put(DATE, date);*/
        BlogPost post = new BlogPost(authorName, message, rating, date);
        // отправляю в базу
        newPostRef.setValue(post);

        return newPostRef.getKey();
    }
    public void read(String id) {
        // настраиваю объект под идентификатор
        Firebase ref = new Firebase(REF_RATING_ALL + "post" + id);
        // подключаю слушателя изменений для указаной ветки "post" + id
        ref.addValueEventListener(new ValueEventListener() {
            // метод следит за изменениями ветки БД и выполняется как минимум один раз
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Log.d("FireBase", ("There are " + snapshot.getChildrenCount() + " blog posts"));
                // очищаю лист постов
                PostFragment.blogPostsList.clear();
                // суммарное значения рейтинга по всем сообщениям
                float ratingSum = 0;
                // перебираю посты из указанной ветки
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        // из поста получаю значение в виде объекта BlogPost post
                        BlogPost post = postSnapshot.getValue(BlogPost.class);
                        // добавляю объект post в лист постов
                        PostFragment.blogPostsList.add(post);
                        Log.d("FireBase", (post.getAuthor() + " - " + post.getMessage() + " - " + post.getRating()) + " - " + post.getDate());
                        // суммирую рейтинг
                        ratingSum += Float.valueOf(post.getRating());
                    } catch (FirebaseException e) {
                        Log.d("FireBase", "The read failed: BlogPost names error ");
                    }
                }

                // CALLBACK
                // обновляю лист
                PostFragment.updateListView();
                // обновляю рейтинг
                PostFragment.updateRatingBar(ratingSum / snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("FireBase", ("The read failed: " + firebaseError.getMessage()));
            }
        });
    }


    public void loadFromCloud(String region) {
        // настраиваю объект
        Firebase ref = new Firebase(REF_DOWNLOAD_ALL + region);

        // подключаю слушателя изменений для указаной ветки
        ref.addValueEventListener(new ValueEventListener() {
            // метод следит за изменениями ветки БД и выполняется как минимум один раз
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // очищаю лист asz
                LabAzs.azsList.clear();
                LabAzs.isInit = false;
                // перебираю посты из указанной ветки
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    try {
                        // получаю значение в виде объекта AZS
                        AZS azs = postSnapshot.getValue(AZS.class);
                        // icon
                        int iconRes = 0;
                        String oilPrice = "-";
                        if (azs.getBrandName() != null
                                && azs.getAddress() != null
                                && azs.getId() != null
                                && azs.getTelephoneNumber()!= null
                                && azs.getLatitude() > 0
                                && azs.getLongitude() > 0) {

                            switch (azs.getBrandName()){
                                case Price.SOCAR:
                                    iconRes = R.drawable.socar_logo;
                                    oilPrice = Price.getPrice(Price.SOCAR);
                                    break;
                                case Price.SHELL:
                                    iconRes = R.drawable.shell_logo;
                                    oilPrice = Price.getPrice(Price.SHELL);
                                    break;
                                case Price.AVIAS:
                                    iconRes = R.drawable.avias_logo;
                                    oilPrice = Price.getPrice(Price.AVIAS);
                                    break;
                                case Price.ANP:
                                    iconRes = R.drawable.anp_logo;
                                    oilPrice = Price.getPrice(Price.ANP);
                                    break;
                                case Price.ULTRA:
                                    iconRes = R.drawable.ultra_logo;
                                    oilPrice = Price.getPrice(Price.ULTRA);
                                    break;
                                case Price.MANGO:
                                    iconRes = R.drawable.mango_logo;
                                    oilPrice = Price.getPrice(Price.ULTRA);
                                    break;
                                case Price.MARSHAL:
                                    iconRes = R.drawable.marshal_logo;
                                    oilPrice = Price.getPrice(Price.MARSHAL);
                                    break;
                                case Price.LUXWEN:
                                    iconRes = R.drawable.luxwen_logo;
                                    oilPrice = Price.getPrice(Price.LUXWEN);
                                    break;
                                case Price.NARODNA:
                                    iconRes = R.drawable.narodna_logo;
                                    oilPrice = Price.getPrice(Price.NARODNA);
                                    break;
                                case Price.INTERLOGOS:
                                    iconRes = R.drawable.interlogos_logo;
                                    oilPrice = Price.getPrice(Price.INTERLOGOS);
                                    break;
                                case Price.UPG:
                                    iconRes = R.drawable.upg_logo;
                                    oilPrice = Price.getPrice(Price.UPG);
                                    break;
                                case Price.UKR_AUTO:
                                    iconRes = R.drawable.ukr_avto_logo;
                                    oilPrice = Price.getPrice(Price.UKR_AUTO);
                                    break;
                                case Price.AMIC:
                                    iconRes = R.drawable.amic_logo;
                                    oilPrice = Price.getPrice(Price.AMIC);
                                    break;
                                case Price.UKR_PETROL:
                                    iconRes = R.drawable.ukr_petrol_logo;
                                    oilPrice = Price.getPrice(Price.UKR_PETROL);
                                    break;
                                case Price.WOG:
                                    iconRes = R.drawable.wog_logo;
                                    oilPrice = Price.getPrice(Price.WOG);
                                    break;
                                case Price.BRSM_NAFTA:
                                    iconRes = R.drawable.brsm_logo;
                                    oilPrice = Price.getPrice(Price.BRSM_NAFTA);
                                    break;
                                case Price.KLO:
                                    iconRes = R.drawable.klo_logo;
                                    oilPrice = Price.getPrice(Price.KLO);
                                    break;
                                case Price.OKKO:
                                    iconRes = R.drawable.okko_logo;
                                    oilPrice = Price.getPrice(Price.OKKO);
                                    break;
                                case Price.TNK:
                                    iconRes = R.drawable.tnk_logo;
                                    oilPrice = Price.getPrice(Price.TNK);
                                    break;
                                case Price.FORMULA_RETAIL:
                                    iconRes = R.drawable.formula_logo;
                                    oilPrice = Price.getPrice(Price.TNK);
                                    break;
                                case Price.GOLD_GEPARD:
                                    iconRes = R.drawable.tnk_gold_gepard_logo;
                                    oilPrice = Price.getPrice(Price.TNK);
                                    break;
                                case Price.UKRNAFTA:
                                    iconRes = R.drawable.ukr_nafta_logo;
                                    oilPrice = Price.getPrice(Price.UKRNAFTA);
                                    break;
                                default:
                                    iconRes = R.drawable.default_logo;
                            }

                            azs.setIconRes(iconRes);
                            azs.setOilPrice(oilPrice);

                            LabAzs.azsList.add(azs);
                        }

                    } catch (FirebaseException e) {
                        Log.d("FireBase", "The read failed: AZS names error ");
                    }
                }

                LabAzs.isInit = true;
                // CALLBACK
                // обновляю лист
                ((AzsFragmentsActivity)mContext).callBackAddAZSMarkers(LabAzs.azsList);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("FireBase", ("The read failed: " + firebaseError.getMessage()));
            }
        });
    }
    /*private static void authentication(final Firebase ref) {
        // Create a handler to handle the result of the authentication
        Firebase.AuthResultHandler authResultHandler = new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                Toast.makeText(mContext, "authentication ok",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                // Authenticated failed with error fireBaseError
                Toast.makeText(mContext, "authentication failed",Toast.LENGTH_SHORT).show();
            }
        };

        // with an email/password combination
        ref.authWithPassword("primakP@example.com", "passwordforfirebase", authResultHandler);

    }*/

}
