## methods in Fragment

- onResume()V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onFragmentResume", "(Landroid/app/Fragment;)V", false);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "android/app/Fragment", "onResume", "()V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(1, 1);
mv.visitEnd();
```

- onPause()V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onFragmentPause", "(Landroid/app/Fragment;)V", false);
mv.visitVarInsn(ALOAD, 0);
mv.visitMethodInsn(INVOKESPECIAL, "android/app/Fragment", "onPause", "()V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(1, 1);
mv.visitEnd();
```

- setUserVisibleHint(Z)V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ILOAD, 1);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "setFragmentUserVisibleHint", "(Ljava/lang/Object;Z)V", false);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ILOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "android/app/Fragment", "setUserVisibleHint", "(Z)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(2, 2);
mv.visitEnd();
```

- onHiddenChanged(Z)V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ILOAD, 1);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onFragmentHiddenChanged", "(Ljava/lang/Object;Z)V", false);
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ILOAD, 1);
mv.visitMethodInsn(INVOKESPECIAL, "android/app/Fragment", "onHiddenChanged", "(Z)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(2, 2);
mv.visitEnd();
```

## methods in interfaces

- onClick(Landroid/view/View;)V

```
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onClick", "(Landroid/view/View;)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(1, 2);
mv.visitEnd();
```

- onClick(Landroid/content/DialogInterface;I)V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ILOAD, 2);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onClick", "(Ljava/lang/Object;Landroid/content/DialogInterface;I)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(3, 3);
mv.visitEnd();
```

- onItemClick(Landroid/widget/AdapterView;Landroid/view/View;IJ)V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ALOAD, 2);
mv.visitVarInsn(ILOAD, 3);
mv.visitVarInsn(LLOAD, 4);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onItemClick", "(Ljava/lang/Object;Landroid/widget/AdapterView;Landroid/view/View;IJ)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(6, 6);
mv.visitEnd();
```

- onItemSelected(Landroid/widget/AdapterView;Landroid/view/View;IJ)V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ALOAD, 2);
mv.visitVarInsn(ILOAD, 3);
mv.visitVarInsn(LLOAD, 4);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onItemSelected", "(Ljava/lang/Object;Landroid/widget/AdapterView;Landroid/view/View;IJ)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(6, 6);
mv.visitEnd();
```

- onCheckedChanged(Landroid/widget/CompoundButton;Z)V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ILOAD, 2);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onCheckedChanged", "(Ljava/lang/Object;Landroid/widget/CompoundButton;Z)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(3, 3);
mv.visitEnd();
```

- onCheckedChanged(Landroid/widget/RadioGroup;I)V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ILOAD, 2);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onCheckedChanged", "(Ljava/lang/Object;Landroid/widget/RadioGroup;I)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(3, 3);
mv.visitEnd();
```


- onChildClick(Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)Z

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ALOAD, 2);
mv.visitVarInsn(ILOAD, 3);
mv.visitVarInsn(ILOAD, 4);
mv.visitVarInsn(LLOAD, 5);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onChildClick", "(Ljava/lang/Object;Landroid/widget/ExpandableListView;Landroid/view/View;IIJ)V", false);
mv.visitInsn(ICONST_0);
mv.visitInsn(IRETURN);
mv.visitMaxs(7, 7);
mv.visitEnd();
```

- onGroupClick(Landroid/widget/ExpandableListView;Landroid/view/View;IJ)Z

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(ALOAD, 2);
mv.visitVarInsn(ILOAD, 3);
mv.visitVarInsn(LLOAD, 4);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onGroupClick", "(Ljava/lang/Object;Landroid/widget/ExpandableListView;Landroid/view/View;IJ)V", false);
mv.visitInsn(ICONST_0);
mv.visitInsn(IRETURN);
mv.visitMaxs(6, 6);
mv.visitEnd();
```

- onRatingChanged(Landroid/widget/RatingBar;FZ)V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitVarInsn(FLOAD, 2);
mv.visitVarInsn(ILOAD, 3);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onRatingChanged", "(Ljava/lang/Object;Landroid/widget/RatingBar;FZ)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(4, 4);
mv.visitEnd();
```

- onStopTrackingTouch(Landroid/widget/SeekBar;)V

```
mv.visitVarInsn(ALOAD, 0);
mv.visitVarInsn(ALOAD, 1);
mv.visitMethodInsn(INVOKESTATIC, "com/plugin/android/PluginAgent", "onStopTrackingTouch", "(Ljava/lang/Object;Landroid/widget/SeekBar;)V", false);
mv.visitInsn(RETURN);
mv.visitMaxs(2, 2);
mv.visitEnd();
```

    
