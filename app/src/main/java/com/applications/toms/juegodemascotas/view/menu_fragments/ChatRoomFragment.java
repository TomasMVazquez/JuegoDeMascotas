package com.applications.toms.juegodemascotas.view.menu_fragments;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.applications.toms.juegodemascotas.R;
import com.applications.toms.juegodemascotas.model.Chat;
import com.applications.toms.juegodemascotas.model.Indexs;
import com.applications.toms.juegodemascotas.model.Owner;
import com.applications.toms.juegodemascotas.util.FragmentTitles;
import com.applications.toms.juegodemascotas.util.Keys;
import com.applications.toms.juegodemascotas.view.adapter.UserAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatRoomFragment extends Fragment implements FragmentTitles {

    public static final String TAG = "ChatRoomFragment";
    //Atributos
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private FirebaseUser fuser;
    private FirebaseFirestore reference;
    private List<String> userList = new ArrayList<>();
    private List<Indexs> indexsList = new ArrayList<>();
    private List<Indexs> list = new ArrayList<>();
    private List<Owner> mUsers;

    private TextView emptyStateChatRoom;

    public ChatRoomFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat_room, container, false);


        emptyStateChatRoom = view.findViewById(R.id.emptyStateChatRoom);

        //Recycler View
        recyclerView = view.findViewById(R.id.recyclerChats);
        recyclerView.hasFixedSize();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //LayoutManager
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(llm);
        userAdapter = new UserAdapter(getContext(),new ArrayList<>(),true);
        recyclerView.setAdapter(userAdapter);

        //Firebase
        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseFirestore.getInstance();

        //Buscar chats del usuario en la base de datos
        reference.collection(Keys.KEY_CHATS).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    Chat chat = dc.getDocument().toObject(Chat.class);

                    if (chat.getSender().equals(fuser.getUid())) {
                        //Guardar con quien se tiene un chat
                        userList.add(chat.getReceiver());
                        if (!containsIndexChat(indexsList,dc.getDocument().getId())) {
                            Indexs indexs = new Indexs(dc.getDocument().getId(),chat.getReceiver());
                            indexsList.add(indexs);
                        }
                    }
                    if (chat.getReceiver().equals(fuser.getUid())) {
                        //Guardar con quien se tiene un chat
                        userList.add(chat.getSender());
                        if (!containsIndexChat(indexsList,dc.getDocument().getId())) {
                            Indexs indexs = new Indexs(dc.getDocument().getId(),chat.getSender());
                            indexsList.add(indexs);
                        }
                    }

                }

                //Buscar index de los chats
                getIndexChats(indexsList);
            }
        });


        return view;
    }

    //Métodos
    private static boolean containsIndexChat(Collection<Indexs> c, String id) {
        for(Indexs o : c) {
            if(o != null && o.getIdChat().equals(id)) {
                return true;
            }
        }
        return false;
    }

    private void sortArrayIndex(List<Indexs> chats){
        Collections.sort(chats, new Comparator<Indexs>() {
            @Override
            public int compare(Indexs o1, Indexs o2) {
                return o1.getIndex() - o2.getIndex();
            }
        });
    }

    private void getIndexChats(List<Indexs> indexs){
        list.clear();
        for (Indexs i:indexs) {
            reference.collection(Keys.KEY_CHATS).document(i.getIdChat()).collection(Keys.KEY_INDEX_COLLECTION).document(fuser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Integer index = Integer.valueOf(document.get(Keys.KEY_INDEX).toString());
                            for (Indexs newInd:indexs) {
                                if (newInd.getIdChat().equals(document.getReference().getParent().getParent().getId())){
                                    newInd.setIndex(index);
                                    list.add(newInd);
                                }
                            }
                            if (list.size() == indexs.size()){
                                sortArrayIndex(list);
                                //Llamar al método leer chats
                                // para buscar los usuarios con los que se está hablando
                                // y llenar el recyclerview
                                readChats();
                            }
                        }
                    }
                }
            });
        }
    }

    private void readChats(){
        mUsers = new ArrayList<>();
        //Buscar a los usuarios de los chats en la base de datos
        reference.collection(Keys.KEY_OWNER).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@androidx.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @androidx.annotation.Nullable FirebaseFirestoreException e) {

                for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
                    Owner user = dc.getDocument().toObject(Owner.class);

                    switch (dc.getType()) {
                        case ADDED:
                            if (userList.contains(user.getUserId())) {
                                if (mUsers.size() != 0) {
                                    if (!containsUser(mUsers,user.getUserId())){
                                        mUsers.add(user);
                                    }
                                } else {
                                    mUsers.add(user);
                                }
                            }
                            break;
                        case MODIFIED:
                            //Para los cambios de estado online/offline de los usuarios
                            if (!user.getUserId().equals(fuser.getUid())){
                                for (Owner u : mUsers) {
                                    if (user.getUserId().equals(u.getUserId())) {
                                        mUsers.add(mUsers.indexOf(u),user);
                                        mUsers.remove(u);
                                        break;
                                    }
                                }
                            }
                            break;
                        case REMOVED:

                            break;
                    }

                }

                //Arreglamos los usuarios en orden
                List<Owner> sortedList = new ArrayList<>();
                for (Indexs one:list) {
                    for (Owner oneUser:mUsers) {
                        if (oneUser.getUserId().equals(one.getSentedTo())){
                            sortedList.add(oneUser);
                        }
                    }
                }

                userAdapter.setmUsers(sortedList);
                if (sortedList.size()>0){
                    emptyStateChatRoom.setVisibility(View.GONE);
                }else {
                    emptyStateChatRoom.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private static boolean containsUser(Collection<Owner> c, String id) {
        for(Owner o : c) {
            if(o != null && o.getUserId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getFragmentTitle() {
        return R.string.chat;
    }

}
