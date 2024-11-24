//package dev.buildtool.satako.test;
//
//import com.github.wintersteve25.tau.components.base.UIComponent;
//import com.github.wintersteve25.tau.components.interactable.Button;
//import com.github.wintersteve25.tau.components.interactable.ListView;
//import com.github.wintersteve25.tau.components.layout.Stack;
//import com.github.wintersteve25.tau.components.utils.Container;
//import com.github.wintersteve25.tau.components.utils.Sized;
//import com.github.wintersteve25.tau.components.utils.Text;
//import com.github.wintersteve25.tau.layout.Layout;
//import com.github.wintersteve25.tau.theme.Theme;
//import com.github.wintersteve25.tau.utils.Size;
//
//import java.util.List;
//
//public class TestTauScreen implements UIComponent {
//    @Override
//    public UIComponent build(Layout layout, Theme theme) {
//        return new Sized(Size.staticSize(200,400),new Container.Builder().withChild(new ListView.Builder().build(new Button.Builder().build(new Text.Builder("1")),new Text.Builder("2"))));
//    }
//}
