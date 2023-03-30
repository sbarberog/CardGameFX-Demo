package application;

import excepciones.NoHayCartasException;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import model.Carta;
import model.Mazo;
import org.controlsfx.control.Notifications;

public class GameViewController {
    @FXML
    private StackPane mesa;
    @FXML
    private VBox vBox;
    @FXML
    private ImageView imgBaraja;
    @FXML
    private ImageView paperBin;
    @FXML
    private StackPane binStack;
    @FXML
    private Label txtCarta;
    @FXML
    private Label txtBorrado;
    @FXML
    private Label txtDeck;
    @FXML
    private Button reset;
    private ImageView vistaBoom;
    private Mazo mazo;
    private int posX, posY, cont;
    private double startX;
    private double startY;
    private double viewOrder;

    @FXML
    private void initialize() {
        viewOrder=100;
        posX = 0;
        posY=0;
        cont=0;
        mazo = new Mazo();
        mazo.barajar();
        Image imgBoom = new Image(CardsApplication.class.getResourceAsStream("/cartas/explosion.gif"));
        vistaBoom = new ImageView();
        vistaBoom.setImage(imgBoom);
        vistaBoom.setVisible(false);
        binStack.getChildren().add(vistaBoom);
        BackgroundImage bg=new BackgroundImage(new Image(CardsApplication.class.getResourceAsStream("/cartas/casino1.png")),
                BackgroundRepeat.REPEAT,BackgroundRepeat.REPEAT, BackgroundPosition.CENTER,BackgroundSize.DEFAULT);
        vBox.setBackground(new Background(bg));
        txtCarta.setText("Game started");
        actualizaTxtMazo();
    }

    private void actualizaTxtMazo() {
        txtDeck.setText("Cards on the deck: "+mazo.numCartas());
    }

    @FXML
    protected void sacaCarta() {
        Carta carta = null;
        try {
            if(mazo.numCartas()==0) {
                throw new NoHayCartasException();
            }
            carta = pideCarta();
        } catch (NoHayCartasException e) {
            Notifications.create()
                    .title("Empty deck")
                    .text("There are no more cards left on the deck.")
                    .owner(mesa)
                    .position(Pos.TOP_LEFT)
                    .hideAfter(Duration.millis(10000))
                    .showWarning();
            throw new RuntimeException(e);
        }
        String filename = "" + carta.imagenCarta() + ".png";
        Image imgCarta = new Image(CardsApplication.class.getResourceAsStream("/cartas/" + filename));
        ImageView vistaCarta = new ImageView(imgCarta);
        vistaCarta.setUserData(carta);
        DropShadow shadow= new DropShadow();
        shadow.setOffsetX(10);
        shadow.setOffsetY(10);
        shadow.setColor(new Color(0,0,0,0.2));
        vistaCarta.setEffect(shadow);
        mesa.getChildren().add(vistaCarta);
        vistaCarta.setViewOrder(viewOrder);
        viewOrder-=0.001;
        setCardEvents(vistaCarta);

        Bounds boundsInScreen = imgBaraja.localToScreen(imgBaraja.getBoundsInLocal());
        double posXMazo=boundsInScreen.getMinX()+100;
        double posYMazo=boundsInScreen.getMinY()-90;
        TranslateTransition tr = new TranslateTransition(Duration.millis(800));
        int altura = (int) (Math.random() * 20);
        tr.setFromY(posYMazo);
        tr.setToY(posY*50+altura);
        tr.setFromX(posXMazo);
        tr.setToX(50 + posX * 150);
        tr.setInterpolator(Interpolator.EASE_OUT);
        RotateTransition rt = new RotateTransition(Duration.millis(800));
//        int angle = (int) (Math.random() * 30) - 15;
        int angle=0;
        rt.setFromAngle(180);
        rt.setToAngle(angle);
        rt.setInterpolator(Interpolator.EASE_OUT);
        ParallelTransition transicion = new ParallelTransition(vistaCarta, tr, rt);
        transicion.play();

        //calculate the position of the next card
        cont++;
        if((double)cont%10==0) {
            posY++;
            if(posY>5) posY=0;
        }
        else if((cont/10)%2==0) posX++;
        else posX--;

        actualizaTxtMazo();
        txtCarta.setText("New card: " + carta + " | height=" + altura + " | angle=" + angle
                + " | positionX=" + posX+" | positionY=" + posY+" | cards out="+cont);
    }

    @FXML
    protected void onResetButtonClick() {
        posX = 0;
        posY=0;
        cont=0;
        viewOrder=100;
        txtCarta.setText("New game started");
        Bounds boundsInScreen = imgBaraja.localToScreen(imgBaraja.getBoundsInLocal());
        double posXMazo=boundsInScreen.getMinX()+100;
        double posYMazo=boundsInScreen.getMinY()-90;
        for (Node img: mesa.getChildren()) {
            mazo.devolverCarta((Carta) img.getUserData());
            if(mazo.numCartas()==52){
                mazo.barajar();
                Notifications.create()
                        .title("New game")
                        .text("Shuffling cards...")
                        .owner(mesa)
                        .position(Pos.TOP_LEFT )
                        .hideAfter(Duration.seconds(10))
                        .showInformation();
            }
            TranslateTransition toDeck= new TranslateTransition();
            toDeck.setToX(posXMazo);
            toDeck.setToY(posYMazo);
            double rand=Math.random()*700;
            toDeck.setDuration(Duration.millis(rand));
            toDeck.setInterpolator(Interpolator.EASE_IN);
            toDeck.setNode(img);
            toDeck.setOnFinished(event -> {
                mesa.getChildren().remove(img);
                event.consume();
            });
            toDeck.play();
        actualizaTxtMazo();
        imgBaraja.setDisable(false);
        imgBaraja.setOpacity(1);
        }

    }

    @FXML
    private Carta pideCarta() throws NoHayCartasException {
        return mazo.solicitarCarta();
    }

    private void zoomNode(Node node, double size) {
        ScaleTransition scale = new ScaleTransition(Duration.millis(100));
        scale.setToX(size);
        scale.setToY(size);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        scale.setNode(node);
        scale.play();
    }

    @FXML
    private void setCardEvents(Node carta) {

        double originalViewOrder= carta.getViewOrder();
        carta.setOnMouseEntered(e -> {
            zoomNode(carta, 1.1);
            carta.setCursor(Cursor.MOVE);
            carta.setViewOrder(0);
        });
        carta.setOnMouseExited(e -> {
            zoomNode(carta, 1);
            carta.setViewOrder(originalViewOrder);
        });
        carta.setOnDragDetected(event -> {
            carta.startFullDrag();
        });
        carta.setOnMousePressed(e -> {
            startX = e.getSceneX() - carta.getTranslateX();
            startY = e.getSceneY() - carta.getTranslateY();
            carta.setMouseTransparent(true);
        });
        carta.setOnMouseDragged(e -> {
            carta.setViewOrder(0);
            double newViewOrder=viewOrder;
            carta.setOnMouseExited(e2 -> {
                zoomNode(carta, 1);
                carta.setViewOrder(newViewOrder);
            });
            viewOrder-=0.001;
            double posX = Math.min(e.getSceneX() - startX, mesa.getWidth() - 120);
            if (posX < 0) posX = 0;
            carta.setTranslateX(posX);
            double posY = Math.min(e.getSceneY() - startY, vBox.getHeight() - 250);
            if (posY < 0) posY = 0;
            carta.setTranslateY(posY);
            Glow glow = new Glow();
            glow.setLevel(0.7);
            paperBin.setEffect(glow);
            paperBin.addEventHandler(MouseDragEvent.MOUSE_DRAG_RELEASED, event -> {
                borrarCarta((Node) event.getGestureSource());
                Timeline timeline = new Timeline(
                        new KeyFrame(Duration.ZERO, time -> {
                            vistaBoom.setVisible(true);
                        }),
                        new KeyFrame(Duration.millis(300), time -> {
                            vistaBoom.setVisible(false);
                        })
                );
                timeline.play();

                event.consume();
            });
        });
        carta.setOnMouseReleased(event -> {
            if (carta.getTranslateY() > mesa.getHeight() - 100) {
                TranslateTransition t = new TranslateTransition(Duration.millis(200));
                t.setNode(carta);
                t.setToY(mesa.getHeight() - 100);
                t.play();
            }
            Glow glow = new Glow();
            glow.setLevel(0);
            paperBin.setEffect(glow);
            carta.setMouseTransparent(false);
        });
//        carta.adde(
//                event -> {
//                    Bounds boundsInScene = imgBaraja.localToScene(imgBaraja.getBoundsInLocal());
//                    double posXMazo=boundsInScene.getCenterX()-50;
//                    double posYMazo=boundsInScene.getCenterY()-150;
//                    TranslateTransition toDeck= new TranslateTransition();
//                    toDeck.setToX(posXMazo);
//                    toDeck.setToY(posYMazo);
//                    toDeck.setDuration(Duration.millis(300));
//                    toDeck.setNode(carta);
//                    toDeck.play();
//                }
//        );
    }

    @FXML
    private void binEntered() {
        Image binOpen = new Image(CardsApplication.class.getResourceAsStream("/cartas/paperbin_open.png"));
        paperBin.setImage(binOpen);
        zoomNode(paperBin, 1.1);
    }

    @FXML
    private void binExited() {
        Image binClosed = new Image(CardsApplication.class.getResourceAsStream("/cartas/paperbin.png"));
        paperBin.setImage(binClosed);
        Glow glow=new Glow();
        glow.setLevel(0);
        paperBin.setEffect(glow);
        zoomNode(paperBin, 1);
    }

    @FXML
    private void pulsaMazo(){
        ScaleTransition scale = new ScaleTransition(Duration.millis(100));
        scale.setFromX(1);
        scale.setFromY(1);
        scale.setToX(1.1);
        scale.setToY(1.1);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        scale.setNode(imgBaraja);
        scale.setAutoReverse(true);
        scale.setCycleCount(2);
//        scale.setOnFinished(event -> {
//            sacaCarta();
//        });
        scale.play();
        sacaCarta();

        if(mazo.numCartas()==0) {
            imgBaraja.setOpacity(0.5);
            imgBaraja.setDisable(true);
            Notifications.create()
                    .title("Empty deck")
                    .text("There are no more cards left on the deck.")
                    .owner(mesa)
                    .position(Pos.TOP_LEFT)
                    .hideAfter(Duration.millis(10000))
                    .showWarning();
        }
    }

    @FXML
    private void deckEntered() {
        Glow glow = new Glow();
        glow.setLevel(0.3);
        imgBaraja.setEffect(glow);
    }

    @FXML
    private void deckExited() {
        Glow glow = new Glow();
        glow.setLevel(0);
        imgBaraja.setEffect(glow);
    }

    @FXML
    private void borrarCarta(Node vistaCarta) {
        Carta carta= (Carta) vistaCarta.getUserData();
        mesa.getChildren().remove(vistaCarta);
        if(mazo.numCartas()==0){
            imgBaraja.setDisable(false);
            imgBaraja.setOpacity(1);
        }
        mazo.devolverCarta(carta);
        actualizaTxtMazo();
        txtBorrado.setText(carta.toString()+" returned to the deck.");
    }

}