package com.lenovo.launcher2.commonui;


import com.lenovo.launcher.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.ListPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

/**
 * 自定义设置Preference的category。google默认的Category无法提供修改样式的接口
 * 
 * @author Zhuhanshan
 * 
 */
public class LeListPreference extends ListPreference {
	private Context mContext;

	public LeListPreference(Context context, AttributeSet attrs,int defStyle) {
		super(context, attrs);
		mContext = context;
		 TypedArray a = context.obtainStyledAttributes(attrs,
	                com.android.internal.R.styleable.ListPreference, 0, 0);
	        mEntries = a.getTextArray(com.android.internal.R.styleable.ListPreference_entries);
	        mEntryValues = a.getTextArray(com.android.internal.R.styleable.ListPreference_entryValues);
	        a.recycle();

	        /* Retrieve the Preference summary attribute since it's private
	         * in the Preference class.
	         */
	        a = context.obtainStyledAttributes(attrs,
	                com.android.internal.R.styleable.Preference, 0, 0);
	        mSummary = a.getString(com.android.internal.R.styleable.Preference_summary);
	        a.recycle();
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		findTextView(view);
	}

	private void findTextView(View v) {
		if (v instanceof ViewGroup) {
			ViewGroup vg1 = (ViewGroup) v;

			for (int j = 0; j < vg1.getChildCount(); j++) {
				View v1 = vg1.getChildAt(j);
				findTextView(v1);
			}
		}else if (v instanceof TextView) {
			//Utilities.setTextTypeface(v,mContext);
		}
	}
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;
    private String mValue;
    private String mSummary;
    private int mClickedDialogEntryIndex;
	private int mWhichButtonClicked;
    

    private int getValueIndex() {
        return findIndexOfValue(mValue);
    }
    
    @Override
    protected void onPrepareDialogBuilder(Builder builder) {
        super.onPrepareDialogBuilder(builder);
        
        if (mEntries == null || mEntryValues == null) {
            throw new IllegalStateException(
                    "ListPreference requires an entries array and an entryValues array.");
        }

        mClickedDialogEntryIndex = getValueIndex();
        builder.setSingleChoiceItems(mEntries, mClickedDialogEntryIndex, 
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        mClickedDialogEntryIndex = which;

                        /*
                         * Clicking on an item simulates the positive button
                         * click, and dismisses the dialog.
                         */
                        LeListPreference.this.onClick(dialog, DialogInterface.BUTTON_POSITIVE);
                        dialog.dismiss();
                    }
        });
        builder.setPositiveButton(null, null);
    }
    @Override
    protected void onClick() {
    	//super.onClick();
    	showSingleChoiceDialog(mContext,this.getKey());
    }

    /**
     * Shows the dialog associated with this Preference. This is normally initiated
     * automatically on clicking on the preference. Call this method if you need to
     * show the dialog on some other event.
     * 
     * @param state Optional instance state to restore on the dialog
     */
    private AlertDialog.Builder mBuilder;
	private int mDialogLayoutResId;
	private AlertDialog mLeosDialog;

    @Override
    protected void showDialog(Bundle state) {
    	showSingleChoiceDialog(mContext,this.getKey());
    }
    /*protected View onCreateDialogView() {
        if (mDialogLayoutResId == 0) {
            return null;
        }
        
        LayoutInflater inflater = LayoutInflater.from(mBuilder.getContext());
        return inflater.inflate(mDialogLayoutResId, null);
    }*/
    void showSingleChoiceDialog(Context context,String sharedPreferenceKey) { 
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        //String sortmode = preferences.getString(sharedPreferenceKey, "");
        

        ListAdapter adapter = new ArrayAdapter<CharSequence>(context, R.layout.select_dialog_item, R.id.select_item_text, mEntries) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View v = super.getView(position, convertView, parent);
                if (getValue().equals(mEntryValues[position])) {
                    ((ImageView) v.findViewById(R.id.select_item_mark)).setSelected(true);
                } else {
                    ((ImageView) v.findViewById(R.id.select_item_mark)).setSelected(false);
                    ((ImageView) v.findViewById(R.id.select_item_detail)).setVisibility(View.INVISIBLE);
                }
                return v;
            }
            @Override
            public int getCount() {
                return super.getCount();
            }
        };
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        
        final LeDialog dialog = new LeDialog(context,R.style.Theme_LeLauncher_Dialog_Shortcut);
        
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setCanceledOnTouchOutside(true);
        
		View contentView// = onCreateDialogView();
        = (LinearLayout)inflater.inflate(R.layout.le_dialog_preference_layout, null);
        
//		TextView title1 = (TextView) contentView
//				.findViewById(R.id.dialog_title);
//		title1.setText(this.getTitle());
		dialog.setLeTitle(this.getTitle());
		
		ListView preferenceList = (ListView)contentView.findViewById(R.id.preference_list);
		preferenceList.setAdapter(adapter);
		preferenceList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				mWhichButtonClicked = position;
				LeListPreference.this.setValue(mEntryValues[position].toString());
				LeListPreference.this.setValueIndex(position);
				if(LeListPreference.this.getOnPreferenceChangeListener()!=null)
					LeListPreference.this.getOnPreferenceChangeListener().onPreferenceChange(LeListPreference.this, mEntryValues[position]);
				LeListPreference.this.setSummary(LeListPreference.this.getEntry());
				dialog.dismiss();
			}
		});
		dialog.setContentView(contentView);
		
//		Window window = dialog.getWindow();
//		window.setGravity(Gravity.CENTER);
		
		        
		dialog.show();
    }
    
    public LeListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
		mContext = context;

        TypedArray a = context.obtainStyledAttributes(attrs,
                com.android.internal.R.styleable.ListPreference, 0, 0);
        mEntries = a.getTextArray(com.android.internal.R.styleable.ListPreference_entries);
        mEntryValues = a.getTextArray(com.android.internal.R.styleable.ListPreference_entryValues);
        a.recycle();

        /* Retrieve the Preference summary attribute since it's private
         * in the Preference class.
         */
        a = context.obtainStyledAttributes(attrs,
                com.android.internal.R.styleable.Preference, 0, 0);
        mSummary = a.getString(com.android.internal.R.styleable.Preference_summary);
        a.recycle();
    }

    public LeListPreference(Context context) {
        this(context, null);
		mContext = context;

    }

    /**
     * Sets the human-readable entries to be shown in the list. This will be
     * shown in subsequent dialogs.
     * <p>
     * Each entry must have a corresponding index in
     * {@link #setEntryValues(CharSequence[])}.
     * 
     * @param entries The entries.
     * @see #setEntryValues(CharSequence[])
     */
/*    public void setEntries(CharSequence[] entries) {
        mEntries = entries;
    }
    
    *//**
     * @see #setEntries(CharSequence[])
     * @param entriesResId The entries array as a resource.
     *//*
    public void setEntries(int entriesResId) {
        setEntries(getContext().getResources().getTextArray(entriesResId));
    }
    
    *//**
     * The list of entries to be shown in the list in subsequent dialogs.
     * 
     * @return The list as an array.
     *//*
    public CharSequence[] getEntries() {
        return mEntries;
    }
    
    *//**
     * The array to find the value to save for a preference when an entry from
     * entries is selected. If a user clicks on the second item in entries, the
     * second item in this array will be saved to the preference.
     * 
     * @param entryValues The array to be used as values to save for the preference.
     *//*
    public void setEntryValues(CharSequence[] entryValues) {
        mEntryValues = entryValues;
    }

    *//**
     * @see #setEntryValues(CharSequence[])
     * @param entryValuesResId The entry values array as a resource.
     *//*
    public void setEntryValues(int entryValuesResId) {
        setEntryValues(getContext().getResources().getTextArray(entryValuesResId));
    }
    
    *//**
     * Returns the array of values to be saved for the preference.
     * 
     * @return The array of values.
     *//*
    public CharSequence[] getEntryValues() {
        return mEntryValues;
    }

    *//**
     * Sets the value of the key. This should be one of the entries in
     * {@link #getEntryValues()}.
     * 
     * @param value The value to set for the key.
     *//*
    public void setValue(String value) {
        mValue = value;
        
        persistString(value);
    }

    *//**
     * Returns the summary of this ListPreference. If the summary
     * has a {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current entry
     * value will be substituted in its place.
     *
     * @return the summary with appropriate string substitution
     *//*
    @Override
    public CharSequence getSummary() {
        final CharSequence entry = getEntry();
        if (mSummary == null || entry == null) {
            return super.getSummary();
        } else {
            return String.format(mSummary, entry);
        }
    }

    *//**
     * Sets the summary for this Preference with a CharSequence.
     * If the summary has a
     * {@linkplain java.lang.String#format String formatting}
     * marker in it (i.e. "%s" or "%1$s"), then the current entry
     * value will be substituted in its place when it's retrieved.
     *
     * @param summary The summary for the preference.
     *//*
    @Override
    public void setSummary(CharSequence summary) {
        super.setSummary(summary);
        if (summary == null && mSummary != null) {
            mSummary = null;
        } else if (summary != null && !summary.equals(mSummary)) {
            mSummary = summary.toString();
        }
    }

    *//**
     * Sets the value to the given index from the entry values.
     * 
     * @param index The index of the value to set.
     *//*
    public void setValueIndex(int index) {
        if (mEntryValues != null) {
            setValue(mEntryValues[index].toString());
        }
    }
    
    *//**
     * Returns the value of the key. This should be one of the entries in
     * {@link #getEntryValues()}.
     * 
     * @return The value of the key.
     *//*
    public String getValue() {
        return mValue; 
    }
    
    *//**
     * Returns the entry corresponding to the current value.
     * 
     * @return The entry corresponding to the current value, or null.
     *//*
    public CharSequence getEntry() {
        int index = getValueIndex();
        return index >= 0 && mEntries != null ? mEntries[index] : null;
    }
    
    *//**
     * Returns the index of the given value (in the entry values array).
     * 
     * @param value The value whose index should be returned.
     * @return The index of the value, or -1 if not found.
     *//*
    public int findIndexOfValue(String value) {
        if (value != null && mEntryValues != null) {
            for (int i = mEntryValues.length - 1; i >= 0; i--) {
                if (mEntryValues[i].equals(value)) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        
        if (positiveResult && mClickedDialogEntryIndex >= 0 && mEntryValues != null) {
            String value = mEntryValues[mClickedDialogEntryIndex].toString();
            if (callChangeListener(value)) {
                setValue(value);
            }
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        setValue(restoreValue ? getPersistedString(mValue) : (String) defaultValue);
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        if (isPersistent()) {
            // No need to save instance state since it's persistent
            return superState;
        }
        
        final SavedState myState = new SavedState(superState);
        myState.value = getValue();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }
         
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        setValue(myState.value);
    }
    
    private static class SavedState extends BaseSavedState {
        String value;
        
        public SavedState(Parcel source) {
            super(source);
            value = source.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeString(value);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }*/
    
}