package su.moy.chernihov.mapapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

@SuppressLint("ValidFragment")
public class PostFragment extends Fragment {
    // индификатор для записи в БД
    private String azs_ID;
    // формат даты для записи в БД
    private static final SimpleDateFormat format = new SimpleDateFormat("yy.MM.dd");

    // адаптер для листа постов полученных из БД
    protected static PostListAdapter adapter;
    // лист постов полученных из БД
    protected static ArrayList<BlogPost> blogPostsList = new ArrayList<>();
    // рейтинг объекта по указанному идентификатору AZS_ID
    protected static RatingBar ratingBar;


    // поля ввода текста для сообщения и для имени автора сообщения
    protected EditText etMessage, etName;
    // кнопка отправки сообщения в БД
    protected Button btnPost;
    // лист постов для отображения
    protected ListView lvMessages;
    // инфлейтер для адаптера листа постов
    protected LayoutInflater inflater;
    // объект для работы с FireBase
    protected FireBaseConnections fireBaseConnection;


    // в конструкторе передаю айдишник заправки
    public PostFragment(String ID) {
     azs_ID = ID;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // сохранение фрагментов включено
        setRetainInstance(true);
        // инициализирую объект для работы с БД
        fireBaseConnection = FireBaseConnections.getInstance(getContext());
        // включаю режим чтения инфы с БД по указанному идентификатору
        fireBaseConnection.read(azs_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // настраиваю вью фрагмента
        View v = inflater.inflate(R.layout.fragment_post, container, false);
        etName = (EditText) v.findViewById(R.id.post_fragment_editText_name);
        etMessage = (EditText) v.findViewById(R.id.post_fragment_edit_message);
        ratingBar = (RatingBar) v.findViewById(R.id.post_fragment_ratingBar);
        // минимальный шаг для рейтинг бара
        ratingBar.setStepSize(0.5f);
        btnPost = (Button) v.findViewById(R.id.post_fragment_btn_post);
        // при нажатии кнопки
        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // беру инфу из всех полей и рейтинг бара
                String authorName = String.valueOf(etName.getText());
                String message = String.valueOf(etMessage.getText());
                String rating = String.valueOf(ratingBar.getRating());
                // провожу валидацию входных данных
                if ((authorName.length() > 0) // длинна имени автора больше нуля и меньше 20 символов
                        && (authorName.length() < 20)
                        && (message.length() > 0) // длинна сообщения больше нуля и меньше 200 символов
                        && (message.length() < 200) // есть оценка по рейтингу
                        && (ratingBar.getRating() > 0)) {
                    // текущая дата
                    Date date = new Date();
                    // делаю запись в БД передаю(идентификатор, имя автора, сообщение, рейтинг заправки, текущую дату)
                    fireBaseConnection.write(azs_ID, authorName, message, rating, format.format(date));
                    // текстовые поля очищаю
                    etName.setText("");
                    etMessage.setText("");
                    // Если данные невалидны то вывожу тост
                } else
                    Toast.makeText(getContext(), "Не корректные данные", Toast.LENGTH_LONG).show();
            }
        });
        // настраиваю лист вью
        lvMessages = (ListView) v.findViewById(R.id.post_fragment_listView_messages);
        // в адаптер передаю лист постов
        adapter = new PostListAdapter(getContext(), blogPostsList);
        lvMessages.setAdapter(adapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        // если лист пуст, то выводиться специальный "пустой вью"
        View view = getActivity().getLayoutInflater().inflate(R.layout.empty_list_posts, null);
        ((ViewGroup) lvMessages.getParent()).addView(view);
        lvMessages.setEmptyView(view);
    }

    @Override
    public void onResume() {
        // обновляю адаптер
        adapter.notifyDataSetChanged();
        super.onResume();
        ((AzsFragmentsActivity)getActivity()).getBtnShowList().setVisibility(View.INVISIBLE);
        ((AzsFragmentsActivity)getActivity()).getRgFilters().setVisibility(View.INVISIBLE);
    }

    // обратный вызов для обновления адаптера
    protected static void updateListView() {
        adapter.notifyDataSetChanged();
    }

    // обратный вызов для обновления рейтинга
    protected static void updateRatingBar(float rating) {
        ratingBar.setRating(rating);
    }

    // Класс адаптера
    private class PostListAdapter extends ArrayAdapter<BlogPost> {
        public PostListAdapter(Context context, ArrayList<BlogPost> recordFilesList) {
            super(context, 0, recordFilesList);
            inflater = LayoutInflater.from(context);
        }

        // получаю вью каждого элемента листа
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.list_item_post, null);
            }
            // получаю объект из листа по указанной позиции
            BlogPost post = getItem(position);
            // вытягиваю все необходимые данные из объекта
            String authorName = post.getAuthor();
            String message = post.getMessage();
            String rating = post.getRating();
            String date = post.getDate();

            // НАСТРАИВАЮ ВСЕ ТЕКСТОВЫЕ ВЬЮШКИ
            // имя автора сообщения
            TextView tvAuthorName = (TextView) convertView.findViewById(R.id.list_item_post_author);
            tvAuthorName.setText(authorName);
            // сообщение
            TextView tvMessage = (TextView) convertView.findViewById(R.id.list_item_post_message);
            tvMessage.setText(message);
            // рейтинг
            TextView tvRating = (TextView) convertView.findViewById(R.id.list_item_post_rating);
            tvRating.setText(rating);
            // дата создания сообщения
            TextView tvDate = (TextView) convertView.findViewById(R.id.list_item_post_date);
            tvDate.setText(date);
            // возвращаю готовое вью
            return convertView;
        }
    }
}
