package com.ThirtyNineEighty.System;

import java.util.ArrayList;

public class Bindable
  implements IBindable
{
  private final ArrayList<ISubprogram> subprograms;
  private volatile boolean initialized;

  public Bindable()
  {
    subprograms = new ArrayList<>();
  }

  @Override
  public boolean isInitialized() { return initialized; }

  @Override
  public void initialize(Object args)
  {
    initialized = true;
  }

  @Override
  public void uninitialize()
  {
    initialized = false;

    for (ISubprogram subprogram : subprograms)
      GameContext.content.unbindProgram(subprogram);
  }

  @Override
  public void enable()
  {
    for (ISubprogram subprogram : subprograms)
      subprogram.enable();
  }

  @Override
  public void disable()
  {
    for (ISubprogram subprogram : subprograms)
      subprogram.disable();
  }

  @Override
  public void bindProgram(ISubprogram subprogram)
  {
    GameContext.content.bindProgram(subprogram);
    subprograms.add(subprogram);
  }

  @Override
  public void unbindProgram(ISubprogram subprogram)
  {
    GameContext.content.unbindProgram(subprogram);
    subprograms.remove(subprogram);
  }
}
