package com.mohammadag.removemotogsecondsimicons;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;
import android.view.View;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class XposedMod implements IXposedHookLoadPackage {

	private static final String[] MOTO_G_VIEW_NAMES = {
		"mMobileActivityView2", "mMobileRoamingView2",
		"mMobileSimView2","mMobileStrengthView2",
		"mMobileTypeView2", "mMobileSlotLabelView2",
		"mMobileViewGroup2"
	};

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.systemui"))
			return;

		XC_MethodHook hook = new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				for (String name : MOTO_G_VIEW_NAMES) {
					try {
						View view = (View) XposedHelpers.getObjectField(param.thisObject, name);
						view.setVisibility(View.GONE);
					} catch (NoSuchFieldError e) { }
				}
			}
		};

		try {
			Class<?> MSimSignalClusterView = XposedHelpers.findClass("com.android.systemui.statusbar.MSimSignalClusterView",
					lpparam.classLoader);
			findAndHookMethod(MSimSignalClusterView, "onAttachedToWindow", hook);
			findAndHookMethod(MSimSignalClusterView, "applySubscription", int.class, hook);
		} catch (Throwable t) {
			// Not a Moto G
		}
	}
}
