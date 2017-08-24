package rbsoftware.friendstagram.workarounds

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View

/**
 * Created by silver_android on 09/10/16.
 */

class FlingBehaviour(context: Context, attrs: AttributeSet) : AppBarLayout.Behavior(context, attrs) {
    private var isPositive: Boolean = false

    override fun onNestedFling(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        var yVelocity = velocityY
        var consumedFling = consumed

        if (yVelocity > 0 && !isPositive || yVelocity < 0 && isPositive) {
            yVelocity *= -1
        }
        if (target is RecyclerView && yVelocity < 0) {
            val firstChild = target.getChildAt(0)
            val childAdapterPosition = target.getChildAdapterPosition(firstChild)
            consumedFling = childAdapterPosition > TOP_CHILD_FLING_THRESHOLD
        }
        return super.onNestedFling(coordinatorLayout, child, target, velocityX, yVelocity, consumedFling)
    }

    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout, child: AppBarLayout, target: View, dx: Int, dy: Int, consumed: IntArray) {
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed)
        isPositive = dy > 0
    }

    companion object {
        private const val TOP_CHILD_FLING_THRESHOLD = 3
    }
}