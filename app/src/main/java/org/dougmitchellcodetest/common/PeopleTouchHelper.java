package org.dougmitchellcodetest.common;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.DisplayMetrics;
import android.view.View;

import org.dougmitchellcodetest.activities.MainActivity;


/**
 * Created by dougmitchell on 11/4/16.
 */

public class PeopleTouchHelper extends ItemTouchHelper.Callback {

    private MainActivity.PeopleAdapter adapter;
    private Paint paint;
    private Bitmap icon;

    public PeopleTouchHelper(Context context, MainActivity.PeopleAdapter adapter) {
        this.adapter = adapter;
        this.paint = new Paint();
        paint.setARGB(80, 255, 0, 0);


        icon = BitmapFactory.decodeResource(
                context.getResources(), android.R.drawable.ic_delete);

    }

    @Override public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof MainActivity.PeopleAdapter.PersonViewHolder)
            return makeFlag(ItemTouchHelper.ACTION_STATE_SWIPE, ItemTouchHelper.LEFT);

        return 0;
    }

    @Override public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // do nothing - not supporting move at this time
        return false;
    }

    @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        adapter.remove(viewHolder.getAdapterPosition());
    }

    @Override public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        View itemView = viewHolder.itemView;

        c.drawRect((float) itemView.getRight() + dX,
                (float) itemView.getTop(),
                (float) itemView.getRight(),
                (float) itemView.getBottom(), paint);

        if (isCurrentlyActive) {
            c.drawBitmap(icon,
                    (float) itemView.getRight() - dpToPx(16) - icon.getWidth(),
                    (float) itemView.getTop() + ((float) itemView.getBottom() - (float) itemView.getTop() - icon.getHeight()) / 2,
                    paint);
        }
    }

    private float dpToPx(float dp) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }
}
