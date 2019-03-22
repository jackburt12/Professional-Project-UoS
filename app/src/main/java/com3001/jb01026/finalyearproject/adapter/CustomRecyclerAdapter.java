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
import android.widget.TextView;

import com.turingtechnologies.materialscrollbar.INameableAdapter;

import org.w3c.dom.Text;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com3001.jb01026.finalyearproject.R;
import com3001.jb01026.finalyearproject.model.Plant;
import com3001.jb01026.finalyearproject.model.PlantType;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.CustomViewHolder> implements INameableAdapter, Filterable {

    //TODO: EFFICIENCY: Reduce calls to AddDividers() (Store in variable instead?)


    private ArrayList<Plant> plantList;
    private ArrayList<Plant> filteredList;
    private Context context;
    private String emptyMessage = "No matches!";
    private AdapterView.OnItemClickListener onItemClickListener;


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

                ArrayList<Plant> filteredArray = new ArrayList<Plant>();

                if(query.isEmpty()) {
                    filteredArray = plantList;
                } else {
                    for (Plant p : plantList) {
                        if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                            filteredArray.add(p);
                        }
                    }
                }

                FilterResults results = new FilterResults();
                results.count = filteredArray.size();
                results.values = filteredArray;
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

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.item_name);
            type = (TextView) itemView.findViewById(R.id.item_type);
            thumbnail = (ImageView) itemView.findViewById(R.id.item_icon);

            divider = (TextView) itemView.findViewById(R.id.encyclopedia_divider_letter);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(null, v, getAdapterPosition(), v.getId());

        }
    }

    public CustomRecyclerAdapter(AdapterView.OnItemClickListener onItemClickListener, ArrayList<Plant> plantList) {
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
    public CustomRecyclerAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View plantView = LayoutInflater.from(context).inflate(R.layout.encyclopedia_item, parent, false);
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
