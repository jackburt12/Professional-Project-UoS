package com3001.jb01026.finalyearproject.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.turingtechnologies.materialscrollbar.INameableAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.activity.MainActivity;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.PlantType;
import com3001.jb01026.finalyearproject.model.PlotSize;

public class EncyclopediaRecyclerAdapter extends RecyclerView.Adapter<EncyclopediaRecyclerAdapter.CustomViewHolder> implements INameableAdapter, Filterable {

    //TODO: EFFICIENCY: Reduce calls to AddDividers() (Store in variable instead?)


    private ArrayList<Plant> plantList;
    private ArrayList<Plant> filteredList;
    private Context context;
    private String emptyMessage = "No matches!";
    private AdapterView.OnItemClickListener onItemClickListener;

    private boolean isCreatingWalk = false;
    private int checkedCounter = 0;
    private int checkedLimit = 7;


    @Override
    public Character getCharacterForElement(int element) {
        if(getValidList().size()!=0){
            return getValidList().get(element).getName().charAt(0);
        }
        return null;
    }

    public Plant getItem(int position) {
        return getValidList().get(position);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String query = constraint.toString();

                String textQuery = query.split("\\|")[0];

                ArrayList<Plant> filteredArray = new ArrayList<Plant>();

                //string querying below
                if(textQuery.isEmpty()||textQuery.equals("null") || textQuery=="") {
                    filteredArray = plantList;
                } else {
                    for (Plant p : plantList) {
                        if (p.getName().toLowerCase().contains(textQuery.toLowerCase())) {
                            filteredArray.add(p);
                        }
                    }
                }

                //checkbox filtering below
                String checkboxQuery = query.split("\\|")[1];
                String filterCategory[] = checkboxQuery.substring(1, checkboxQuery.length()-1).split("\\]\\[");
                
                ArrayList<Plant> finalArray = new ArrayList<Plant>();


                List<List<Boolean>> categoryBools = new ArrayList<>();
                List<Boolean> bools;
                for(int i = 0; i < filterCategory.length; i++) {
                    String[] temp = filterCategory[i].split("\\s*,\\s*");
                    bools = new ArrayList<>();
                    for(int j = 0; j < temp.length; j++) {
                        boolean bool = new Boolean(temp[j]);
                        bools.add(bool);


                    }
                    categoryBools.add(bools);
                }

                for(Plant p : filteredArray) {
                    boolean add = true;
                    switch (p.getPlotSize()) {
                        case SMALL:
                            if(!categoryBools.get(0).get(0)) {
                                add = false;
                            }
                            break;
                        case MEDIUM:
                            if(!categoryBools.get(0).get(1)) {
                                add = false;
                            }
                            break;
                        case LARGE:
                            if(!categoryBools.get(0).get(2)) {
                                add = false;
                            }
                            break;
                    }
                    switch (p.getExpertise()) {
                        case BEGINNER:
                            if(!categoryBools.get(1).get(0)) {
                                add = false;
                            }
                            break;
                        case EASY:
                            if(!categoryBools.get(1).get(1)) {
                                add = false;
                            }
                            break;
                        case MEDIUM:
                            if(!categoryBools.get(1).get(2)) {
                                add = false;
                            }
                            break;
                        case ADVANCED:
                            if(!categoryBools.get(1).get(3)) {
                                add = false;
                            }
                            break;
                        case EXPERT:
                            if(!categoryBools.get(1).get(4)) {
                                add = false;
                            }
                            break;
                    }
                    switch (p.getType()) {
                        case FRUIT:
                            if(!categoryBools.get(2).get(0)) {
                                add = false;
                            }
                            break;
                        case VEGETABLE:
                            if(!categoryBools.get(2).get(1)) {
                                add = false;
                            }
                            break;
                        case HERB:
                            if(!categoryBools.get(2).get(2)) {
                                add = false;
                            }
                            break;
                    }
                    switch (p.getCareFrequency()) {
                        case OCCASIONALLY:
                            if(!categoryBools.get(3).get(0)) {
                                add = false;
                            }
                            break;
                        case MONTHLY:
                            if(!categoryBools.get(3).get(1)) {
                                add = false;
                            }
                            break;
                        case FORTNIGHTLY:
                            if(!categoryBools.get(3).get(2)) {
                                add = false;
                            }
                            break;
                        case WEEKLY:
                            if(!categoryBools.get(3).get(3)) {
                                add = false;
                            }
                            break;
                        case DAILY:
                            if(!categoryBools.get(3).get(4)) {
                                add = false;
                            }
                            break;
                    }
                    if(add) {
                        finalArray.add(p);
                    }

                }

                FilterResults results = new FilterResults();
                results.count = finalArray.size();
                results.values = finalArray;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (ArrayList<Plant>) results.values;

                if(filteredList.size() ==0) {
                    filteredList.add(new Plant(emptyMessage, PlantType.DIVIDER, ""));
                }
                notifyDataSetChanged();
            }
        };
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name;
        TextView type;
        ImageView thumbnail;
        TextView divider;
        RadioButton radioButton;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_name);
            type = (TextView) itemView.findViewById(R.id.item_type);
            thumbnail = (ImageView) itemView.findViewById(R.id.item_icon);
            radioButton = (RadioButton) itemView.findViewById(R.id.radio_button);

            divider = (TextView) itemView.findViewById(R.id.encyclopedia_divider_letter);

            itemView.setOnClickListener(this);

        }

        void bind(int position) {
            if(radioButton!=null) {
                if (getValidList().get(position).isChecked()) {
                    radioButton.setChecked(true);
                }
                else {
                    radioButton.setChecked(false);
                }
            }
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(null, v, getAdapterPosition(), v.getId());
            int adapterPosition = getAdapterPosition();

            if(radioButton!=null) {
                if (getValidList().get(adapterPosition).isChecked()) {
                    radioButton.setChecked(false);
                    getValidList().get(adapterPosition).setChecked(false);
                    checkedCounter--;
                }
                else {
                    if(checkedCounter >= checkedLimit) {
                        Toast.makeText(context,"Maximum amount of points reached! Please deselect a different point to select a new one.",Toast.LENGTH_SHORT).show();
                    } else {
                        radioButton.setChecked(true);
                        getValidList().get(adapterPosition).setChecked(true);
                        checkedCounter++;
                    }
                }
            }


        }
    }

    public EncyclopediaRecyclerAdapter(AdapterView.OnItemClickListener onItemClickListener, ArrayList<Plant> plantList) {
        this.plantList = plantList;
        this.onItemClickListener = onItemClickListener;

    }

    @Override
    public int getItemViewType(int position) {
        int viewType;
        if(getValidList().get(position).getType() == PlantType.DIVIDER) {
            viewType = 1;
        } else {
            viewType = 0;
        }

        return viewType;
    }

    @NonNull
    @Override
    public EncyclopediaRecyclerAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        MainActivity mainActivity = (MainActivity) context;
        isCreatingWalk = mainActivity.isCreatingWalk();

        View plantView;
        if(isCreatingWalk) {
            plantView = LayoutInflater.from(context).inflate(R.layout.encyclopedia_item_selectable, parent, false);
        } else {
            plantView = LayoutInflater.from(context).inflate(R.layout.encyclopedia_item, parent, false);

        }

        View letterView = LayoutInflater.from(context).inflate(R.layout.encyclopedia_divider, parent, false);

        switch (viewType) {
            case 0:
                return new CustomViewHolder(plantView);
            case 1:
                return new CustomViewHolder(letterView);
            default:
                return new CustomViewHolder(plantView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        ArrayList<Plant> finalList = getValidList();

        Plant p = finalList.get(position);
        if(p.getType() == PlantType.DIVIDER) {
            holder.divider.setText(p.getName());
        } else {
            holder.name.setText(p.getName());
            holder.type.setText(p.getType().toString());
            String s = "tn_" + getValidList().get(position).getImageID();
            holder.thumbnail.setImageResource(context.getResources().getIdentifier(s, "drawable", context.getPackageName()));
        }

        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (getValidList()).size();
    }


    private ArrayList<Plant> AddDividers(ArrayList<Plant> list) {
        ArrayList<Plant> dividedArray = new ArrayList<>();

        if(list.get(0).getName() != emptyMessage)
            dividedArray.add(new Plant(Character.toString(list.get(0).getName().charAt(0)), PlantType.DIVIDER, ""));

        for(int i = 0; i<list.size(); i++) {
            dividedArray.add(list.get(i));
            if(i!=list.size()-1) {
                if (list.get(i).compareTo(list.get(i + 1))) {
                    dividedArray.add(new Plant(Character.toString(list.get(i + 1).getName().charAt(0)), PlantType.DIVIDER, ""));
                }
            }
        }

        return dividedArray;
    }

    private ArrayList<Plant> getValidList() {
        if(filteredList!= null) {
            return AddDividers(filteredList);
        } else {
            return AddDividers(plantList);
        }
    }

}
