package br.com.caelum.iogi;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SyntheticAttribute;

public class GenerateClassfileWithSyntheticConstructor {
   public static void main(String[] args) throws Exception {
      ClassPool cp = ClassPool.getDefault();
      CtClass ctClass = cp.get("WithSyntheticConstructor");
      CtConstructor constructor = ctClass.getConstructors()[0];
      ConstPool constPool = new ConstPool("WithSyntheticConstructor");
      
      SyntheticAttribute isSynthetic = new SyntheticAttribute(constPool);
      constructor.setAttribute(isSynthetic.getName(), isSynthetic.get());
      
      System.out.println("Saving");
      ctClass.writeFile("testBytecode");
      System.out.println("Saved");
   }
}
