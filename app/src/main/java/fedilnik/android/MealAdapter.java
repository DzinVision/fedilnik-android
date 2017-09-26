package fedilnik.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import fedilnik.android.data.Meal;

public class MealAdapter extends RecyclerView.Adapter<MealViewHolder> {
    public List<Meal> meals;
    private Context context;

    public MealAdapter(Context context) {
        this.context = context;
    }

    @Override
    public MealViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MealViewHolder holder, int position) {
        holder.bindMeal(meals.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }
}

class MealViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView content;


    MealViewHolder(View itemView) {
        super(itemView);

        title = itemView.findViewById(R.id.meal_title);
        content = itemView.findViewById(R.id.meal_content);
    }

    void bindMeal(Meal meal) {
        title.setText(meal.getTitle());
        StringBuilder builder = new StringBuilder();
        for (String i : meal.getContent()) {
            builder.append(i);
            builder.append(", ");
        }
        builder.delete(builder.length() - 2, builder.length());
        content.setText(builder.toString());
    }
}
