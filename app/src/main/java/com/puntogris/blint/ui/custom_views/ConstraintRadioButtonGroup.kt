package com.puntogris.blint.ui.custom_views

import android.content.Context
import android.util.AttributeSet
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.annotation.IdRes
import androidx.constraintlayout.widget.ConstraintHelper
import androidx.constraintlayout.widget.ConstraintLayout
import java.util.*


/**
 * Usage example:
 * <pre>`<android.support.constraint.ConstraintLayout
 * ...
 * <com.a0soft.gphone.base.widget.constraint.blRadioGroup
 * android:layout_width="wrap_content"
 * android:layout_height="wrap_content"
 * app:constraint_referenced_ids="radioButton1, radioButton2" />
 * </android.support.constraint.ConstraintLayout>
`</pre> *
 */
class ConstraintRadioButtonGroup : ConstraintHelper {
    /**
     *
     * Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.
     */
    interface OnCheckedChangeListener {
        /**
         *
         * Called when the checked radio button has changed. When the
         * selection is cleared, nCheckedId is -1.
         *
         * @param rg the group in which the checked radio button has changed
         * @param nCheckedId the unique identifier of the newly checked radio button
         */
        fun OnCheckedChanged(rg: ConstraintRadioButtonGroup?, @IdRes nCheckedId: Int)
    }

    private var m_listenerOnCheckedChange: OnCheckedChangeListener? = null
    private val m_vRadioButtons = ArrayList<RadioButton>()
    private var m_bSkipCheckingViewsRecursively = false
    private var m_nCurrentSelectedViewId = -1
    private var m_nSelectedViewIdBeforePreLayout = -1
    private val m_listenerRadioButton =
        CompoundButton.OnCheckedChangeListener { vButton, bIsChecked ->
            if (m_bSkipCheckingViewsRecursively) return@OnCheckedChangeListener
            if (m_nCurrentSelectedViewId != -1) {
                // uncheck the checked button
                m_bSkipCheckingViewsRecursively = true
                for (v in m_vRadioButtons) {
                    if (v.id == m_nCurrentSelectedViewId) {
                        v.isChecked = false
                        break
                    }
                }
                m_bSkipCheckingViewsRecursively = false
            }
            _SetCurrentSelectedViewId(vButton.id)
        }

    //////////////////////////////////////////////
    constructor(ctx: Context?) : super(ctx) {}
    constructor(ctx: Context?, attrs: AttributeSet?) : super(ctx, attrs) {}
    constructor(ctx: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        ctx,
        attrs,
        defStyleAttr
    )

    override fun init(attrs: AttributeSet) {
        super.init(attrs)
        mUseViewMeasure = false
    }

    override fun updatePreLayout(container: ConstraintLayout) {
        super.updatePreLayout(container)
        for (i in 0 until mCount) {
            val nId = mIds[i]
            val v = container.getViewById(nId)
            if (v is RadioButton) {
                m_vRadioButtons.add(v)
                v.setOnCheckedChangeListener(m_listenerRadioButton)
            }
        }
        if (m_nSelectedViewIdBeforePreLayout != -1) Check(m_nSelectedViewIdBeforePreLayout)
    }


    override fun updatePostLayout(container: ConstraintLayout) {
        val params = layoutParams
        params.width = 0
        params.height = 0
        super.updatePostLayout(container)
    }

    fun ClearCheck() {
        if (m_nCurrentSelectedViewId != -1) {
            // uncheck the checked button
            m_bSkipCheckingViewsRecursively = true
            for (v in m_vRadioButtons) {
                if (v.id == m_nCurrentSelectedViewId) {
                    v.isChecked = false
                    break
                }
            }
            m_bSkipCheckingViewsRecursively = false
            _SetCurrentSelectedViewId(-1)
        }
    }

    /**
     * @return selected RadioButton id, -1 if no checked button
     */
    @IdRes
    fun GetCheckedRadioButtonId(): Int {
        return m_nCurrentSelectedViewId
    }

    /**
     * @param nRadioButtonId set it as checked
     */
    fun Check(@IdRes nRadioButtonId: Int) {
        val bIsBeforePreLayout = m_vRadioButtons.isEmpty()
        m_nSelectedViewIdBeforePreLayout = if (bIsBeforePreLayout) nRadioButtonId else -1
        var bFound = false
        m_bSkipCheckingViewsRecursively = true
        for (v in m_vRadioButtons) {
            if (v.id == nRadioButtonId) {
                v.isChecked = true
                bFound = true
            } else v.isChecked = false
        }
        m_bSkipCheckingViewsRecursively = false
        if (!bIsBeforePreLayout) _SetCurrentSelectedViewId(if (bFound) nRadioButtonId else -1)
    }

    /**
     *
     * Register a callback to be invoked when the checked radio button
     * changes in this group.
     *
     * @param listener the callback to call on checked state change
     */
    fun SetOnCheckedChangeListener(listener: OnCheckedChangeListener?) {
        m_listenerOnCheckedChange = listener
    }

    private fun _SetCurrentSelectedViewId(@IdRes nRadioBtnId: Int) {
        if (m_nCurrentSelectedViewId != nRadioBtnId) {
            m_nCurrentSelectedViewId = nRadioBtnId
            if (m_listenerOnCheckedChange != null) m_listenerOnCheckedChange!!.OnCheckedChanged(
                this,
                nRadioBtnId
            )
        }
    }
}