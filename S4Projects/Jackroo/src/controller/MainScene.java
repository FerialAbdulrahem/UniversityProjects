package controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import engine.board.Cell;
import engine.board.CellType;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainScene {
    // Screen dimensions for responsive layout
    public DoubleProperty screenWidth = new SimpleDoubleProperty();
    public DoubleProperty screenHeight = new SimpleDoubleProperty();
    private DoubleProperty scaleFactorX = new SimpleDoubleProperty();
    private DoubleProperty scaleFactorY = new SimpleDoubleProperty();
    private static final double REFERENCE_WIDTH = 1869.0;
    private static final double REFERENCE_HEIGHT = 954.0;

    // Main container
    public AnchorPane root;

    // Central octagon
    public Polygon octagon;

    // Consolidated array for all circle cells (100 elements)
    public Circle[] allCircleCells = new Circle[100];
    public int card;

    // Player groups and their components
    public static class PlayerGroup {
        public Group group;
        public GridPane[] circleGrids = new GridPane[4];
        public GridPane centerGrid;
        public Label label;
        public Circle extraCircle;
    }

    public PlayerGroup cpu1Group = new PlayerGroup();
    public PlayerGroup cpu2Group = new PlayerGroup();
    public PlayerGroup cpu3Group = new PlayerGroup();
    public PlayerGroup humanGroup = new PlayerGroup();

    // Additional groups
    public static class AdditionalGroup {
        public Group group;
        public GridPane grid1;
        public GridPane grid2;
    }

    public AdditionalGroup group1 = new AdditionalGroup();
    public AdditionalGroup group2 = new AdditionalGroup();
    public AdditionalGroup group3 = new AdditionalGroup();
    public AdditionalGroup group4 = new AdditionalGroup();

    // Grid panes with circles
    public GridPane[] gridPanes = new GridPane[3];

    // UI elements
    public Rectangle firePit, deckRect;
    public Label firePitLabel, deckLabel, deckRemainingLabel;
    // FirePit card display
    public StackPane firePitStack;
    public Label firePitCardRank, firePitCardSuit, firePitCardName;
    // Deck stack visual (3 offset rects)
    public Rectangle deckShadow1, deckShadow2;
    public Button playButton;
    public Label currentPlayerLabel, nextPlayerLabel;
    public Label remainingCards1, remainingCards2, remainingCards3, remainingCards4;
    public GridPane controlGrid;
    public CheckBox[] checkBoxes = new CheckBox[4];
    public Label[] controlLabels = new Label[4];
    // Card visual panels (one per card slot)
    public StackPane[] cardPanels = new StackPane[4];
    public Circle[] miscCircles = new Circle[4];
    public Label humanLabel, cpu3Label, cpu2Label, cpu1Label;
    public Label miscLabel;
    public CircleGrid c;

    // Action panel shown below the card area
    public VBox actionPanel;
    public Button discardButton;   // for Ten / Queen discard action
    public Button splitButton;     // for Seven split action
    public Spinner<Integer> splitSpinner; // 1-6 spinner for split
    public Label splitLabel;

    // Constructor to initialize screen dimensions
    public MainScene() {
        screenWidth.set(Screen.getPrimary().getVisualBounds().getWidth());
        screenHeight.set(Screen.getPrimary().getVisualBounds().getHeight());
        updateLayout();
    }

    private double calcX(double reference) {
        double centerOffset = 0;
        if ((screenWidth.get() / screenHeight.get()) > (REFERENCE_WIDTH / REFERENCE_HEIGHT)) {
            centerOffset = (screenWidth.get() - (REFERENCE_WIDTH * scaleFactorY.get())) / 2;
        }
        return reference * scaleFactorX.get() + centerOffset;
    }

    private double calcY(double reference) {
        double centerOffset = 0;
        if ((screenWidth.get() / screenHeight.get()) < (REFERENCE_WIDTH / REFERENCE_HEIGHT)) {
            centerOffset = (screenHeight.get() - (REFERENCE_HEIGHT * scaleFactorX.get())) / 2;
        }
        return reference * scaleFactorY.get() + centerOffset;
    }

    private double calcSize(double reference) {
        return reference * Math.min(scaleFactorX.get(), scaleFactorY.get());
    }

    private Font calcFont(double fontSize) {
        return Font.font(fontSize * Math.min(scaleFactorX.get(), scaleFactorY.get()));
    }

    private Font calcFont(String fontFamily, FontWeight weight, double fontSize) {
        return Font.font(fontFamily, weight, fontSize * Math.min(scaleFactorX.get(), scaleFactorY.get()));
    }

    public void updateLayout() {
        double scaleX = screenWidth.get() / REFERENCE_WIDTH;
        double scaleY = screenHeight.get() / REFERENCE_HEIGHT;
        if (scaleX > scaleY) {
            scaleFactorX.set(scaleY);
            scaleFactorY.set(scaleY);
        } else {
            scaleFactorX.set(scaleX);
            scaleFactorY.set(scaleX);
        }
    }

    public Scene createGameScene() {
        screenWidth.set(Screen.getPrimary().getVisualBounds().getWidth());
        screenHeight.set(Screen.getPrimary().getVisualBounds().getHeight());
        updateLayout();

        root = new AnchorPane();
        root.setLayoutX(0.0);
        root.setLayoutY(0.0);
        root.setPrefHeight(screenHeight.get());
        root.setPrefWidth(screenWidth.get());

        AnchorPane gameBoard = new AnchorPane();
        gameBoard.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null)));

        AnchorPane.setTopAnchor(gameBoard, 0.0);
        AnchorPane.setBottomAnchor(gameBoard, 0.0);
        AnchorPane.setLeftAnchor(gameBoard, 0.0);
        AnchorPane.setRightAnchor(gameBoard, 0.0);

        root.getChildren().add(gameBoard);
        root.setBackground(new Background(new BackgroundFill(Color.ANTIQUEWHITE, null, null)));

        // Board
        octagon = createOctagon();
        c = new CircleGrid();
        c.setScaleX(calcSize(1.4));
        c.setScaleY(calcSize(1));
        c.setTranslateX(calcX(120.0));
        c.setLayoutX(calcX(300.0));
        c.setLayoutY(calcY(160.0));
        gameBoard.getChildren().addAll(octagon, c);

        // Fire pit
        firePit = createFirePit();
        firePitLabel = createFirePitLabel();
        firePitStack = createFirePitStack();
        gameBoard.getChildren().addAll(firePit, firePitStack);

        // Player name labels
        humanLabel = createHumanLabel();
        cpu3Label = createCPU3Label();
        cpu2Label = createCPU2Label();
        cpu1Label = createCPU1Label();
        gameBoard.getChildren().addAll(humanLabel, cpu3Label, cpu2Label, cpu1Label);

        // Card hand panel (replaces old controlGrid with checkboxes+labels)
        buildCardHandPanel(gameBoard);

        // Action panel (discard / split) – initially hidden
        buildActionPanel(gameBoard);

        // Remaining cards
        remainingCards1 = createRemainingCardsLabel("", calcX(1111.0), calcY(16.0));
        remainingCards2 = createRemainingCardsLabel("", calcX(50.0), calcY(854.0));
        remainingCards3 = createRemainingCardsLabel("", calcX(48.0), calcY(14.0));
        remainingCards4 = createRemainingCardsLabel("", calcX(48.0), calcY(18.0));
        gameBoard.getChildren().addAll(remainingCards1, remainingCards2, remainingCards3, remainingCards4);

        // Play button
        playButton = createPlayButton();
        initializePlayButtonHandler();
        gameBoard.getChildren().add(playButton);

        // Turn labels
        currentPlayerLabel = createCurrentPlayerLabel();
        nextPlayerLabel = createNextPlayerLabel();
        gameBoard.getChildren().addAll(currentPlayerLabel, nextPlayerLabel);

        // Deck
        deckShadow1 = createDeckShadow(calcSize(6), calcSize(6));
        deckShadow2 = createDeckShadow(calcSize(3), calcSize(3));
        deckRect = createDeckRect();
        deckLabel = createDeckLabel();
        deckRemainingLabel = createDeckRemainingLabel();
        gameBoard.getChildren().addAll(deckShadow1, deckShadow2, deckRect, deckLabel, deckRemainingLabel);

        // Misc
        miscLabel = createMiscLabel();
        miscCircles[0] = createMiscCircle(calcX(780.0), calcY(50.0));
        miscCircles[1] = createMiscCircle(calcX(1160.0), calcY(589.0));
        miscCircles[2] = createMiscCircle(calcX(850.0), calcY(950.0));
        miscCircles[3] = createMiscCircle(calcX(260.0), calcY(550.0));
        gameBoard.getChildren().addAll(miscLabel, miscCircles[0], miscCircles[1], miscCircles[2], miscCircles[3]);

        Scene scene = new Scene(root);
        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            screenWidth.set(newVal.doubleValue());
            updateLayout();
            updateAllComponents();
        });
        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            screenHeight.set(newVal.doubleValue());
            updateLayout();
            updateAllComponents();
        });

        return scene;
    }

    // ─── Card Hand Panel ────────────────────────────────────────────────────────

    /**
     * Builds the 4-card visual hand panel in the bottom-right area.
     * Each card is a StackPane styled like a playing card (rectangle + rank/suit/description).
     */
    private void buildCardHandPanel(AnchorPane gameBoard) {
        // We keep checkBoxes[] for selection tracking but make them invisible –
        // selection is driven by clicking the card panel instead.
        controlGrid = new GridPane();
        controlGrid.setLayoutX(calcX(1270.0));
        controlGrid.setLayoutY(calcY(600.0));
        controlGrid.setHgap(calcSize(14));
        controlGrid.setVgap(0);

        for (int i = 0; i < 4; i++) {
            // Invisible checkbox still used for state tracking
            checkBoxes[i] = new CheckBox();
            checkBoxes[i].setVisible(false);
            checkBoxes[i].setManaged(false);

            // Invisible label still used by existing update helpers
            controlLabels[i] = new Label();
            controlLabels[i].setVisible(false);
            controlLabels[i].setManaged(false);

            cardPanels[i] = buildCardPanel(i);
            controlGrid.add(cardPanels[i], i, 0);
        }

        gameBoard.getChildren().add(controlGrid);
    }

    /**
     * Builds a single visual card StackPane for slot index.
     */
    public StackPane buildCardPanel(int index) {
        double cardW = calcSize(110);
        double cardH = calcSize(155);

        Rectangle bg = new Rectangle(cardW, cardH);
        bg.setArcWidth(calcSize(12));
        bg.setArcHeight(calcSize(12));
        bg.setFill(Color.WHITE);
        bg.setStroke(Color.DARKGRAY);
        bg.setStrokeWidth(calcSize(2));

        // Top-left rank label
        Label rankLabel = new Label("?");
        rankLabel.setFont(calcFont("Arial", FontWeight.BOLD, 18));
        rankLabel.setTextFill(Color.BLACK);
        StackPane.setAlignment(rankLabel, Pos.TOP_LEFT);
        rankLabel.setTranslateX(calcSize(8));
        rankLabel.setTranslateY(calcSize(6));

        // Centre suit symbol (large)
        Label suitLabel = new Label("♠");
        suitLabel.setFont(Font.font("Arial", FontWeight.BOLD, calcSize(36)));
        suitLabel.setTextFill(Color.BLACK);
        StackPane.setAlignment(suitLabel, Pos.CENTER);
        suitLabel.setTranslateY(calcSize(-10));

        // Description at bottom
        Label descLabel = new Label("");
        descLabel.setFont(calcFont("Arial", FontWeight.NORMAL, 9));
        descLabel.setTextFill(Color.DIMGRAY);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(cardW - calcSize(10));
        descLabel.setTextAlignment(TextAlignment.CENTER);
        StackPane.setAlignment(descLabel, Pos.BOTTOM_CENTER);
        descLabel.setTranslateY(calcSize(-6));

        // "Card N" placeholder at centre-top
        Label cardNumLabel = new Label("Card " + (index + 1));
        cardNumLabel.setFont(calcFont("Arial", FontWeight.BOLD, 11));
        cardNumLabel.setTextFill(Color.GRAY);
        StackPane.setAlignment(cardNumLabel, Pos.TOP_CENTER);
        cardNumLabel.setTranslateY(calcSize(6));

        StackPane panel = new StackPane(bg, rankLabel, suitLabel, descLabel, cardNumLabel);
        panel.setPrefSize(cardW, cardH);
        panel.setMaxSize(cardW, cardH);

        // Store sub-labels as userData list so Main can update them
        panel.setUserData(new Label[]{rankLabel, suitLabel, descLabel, cardNumLabel});

        // Visual selection effect: clicking the panel selects the card
        final int idx = index;
        panel.setOnMouseClicked(e -> {
            if (!panel.isVisible()) return;
            boolean wasSelected = checkBoxes[idx].isSelected();
            // Deselect all others
            for (int k = 0; k < 4; k++) {
                checkBoxes[k].setSelected(false);
                highlightCard(k, false);
            }
            // Toggle this one
            if (!wasSelected) {
                checkBoxes[idx].setSelected(true);
                highlightCard(idx, true);
            }
        });

        // Hover effect
        panel.setOnMouseEntered(e -> {
            if (panel.isVisible() && !checkBoxes[idx].isSelected()) {
                bg.setStroke(Color.STEELBLUE);
                bg.setStrokeWidth(calcSize(3));
            }
        });
        panel.setOnMouseExited(e -> {
            if (!checkBoxes[idx].isSelected()) {
                bg.setStroke(Color.DARKGRAY);
                bg.setStrokeWidth(calcSize(2));
            }
        });

        return panel;
    }

    /** Highlights or un-highlights the card panel at the given index. */
    public void highlightCard(int index, boolean selected) {
        if (cardPanels[index] == null) return;
        Rectangle bg = (Rectangle) cardPanels[index].getChildren().get(0);
        if (selected) {
            bg.setStroke(Color.GOLD);
            bg.setStrokeWidth(calcSize(4));
            bg.setFill(Color.web("#fffde7"));
        } else {
            bg.setStroke(Color.DARKGRAY);
            bg.setStrokeWidth(calcSize(2));
            bg.setFill(Color.WHITE);
        }
    }

    /**
     * Updates a card panel's visuals (rank, suit, description).
     * Called from Main whenever the hand changes.
     *
     * @param index       card slot index (0-3)
     * @param rankStr     e.g. "A", "K", "10", "J", "Q", "7"
     * @param suitSymbol  e.g. "♠", "♥", "♦", "♣"  (empty string for wild cards)
     * @param suitColor   Color.RED for hearts/diamonds, Color.BLACK for clubs/spades
     * @param description short description text
     * @param cardTitle   full card name shown at top centre
     */
    public void updateCardPanel(int index, String rankStr, String suitSymbol,
                                Color suitColor, String description, String cardTitle) {
        if (cardPanels[index] == null) return;
        Label[] labels = (Label[]) cardPanels[index].getUserData();
        // labels: [rankLabel, suitLabel, descLabel, cardNumLabel]
        labels[0].setText(rankStr);
        labels[0].setTextFill(suitColor);
        labels[1].setText(suitSymbol);
        labels[1].setTextFill(suitColor);
        labels[2].setText(description);
        labels[3].setText(cardTitle);
        labels[3].setTextFill(suitColor.equals(Color.RED) ? Color.CRIMSON : Color.DARKSLATEBLUE);

        // Make panel visible and reset selection state
        cardPanels[index].setVisible(true);
        checkBoxes[index].setSelected(false);
        highlightCard(index, false);
    }

    /** Hides a card panel (card has been played). */
    public void hideCardPanel(int index) {
        if (cardPanels[index] != null) {
            cardPanels[index].setVisible(false);
            checkBoxes[index].setSelected(false);
        }
    }

    // ─── Action Panel (Discard / Split) ─────────────────────────────────────────

    /**
     * Builds the action panel placed below the card hand.
     * Contains: Discard button (for Ten/Queen) and Split row (for Seven).
     */
    private void buildActionPanel(AnchorPane gameBoard) {
        actionPanel = new VBox(calcSize(10));
        actionPanel.setLayoutX(calcX(1270.0));
        actionPanel.setLayoutY(calcY(770.0));
        actionPanel.setAlignment(Pos.CENTER_LEFT);
        actionPanel.setPadding(new Insets(calcSize(8)));
        actionPanel.setBackground(new Background(new BackgroundFill(
                Color.web("#f0f4ff"), new CornerRadii(calcSize(8)), Insets.EMPTY)));
        actionPanel.setBorder(new Border(new BorderStroke(
                Color.SLATEGRAY, BorderStrokeStyle.SOLID, new CornerRadii(calcSize(8)), new BorderWidths(calcSize(1.5)))));
        actionPanel.setVisible(false);

        // Discard button (Ten / Queen)
        discardButton = new Button("🗑  Discard Card");
        styleActionButton(discardButton, "#e53935", "#b71c1c");
        discardButton.setVisible(false);

        // Split row (Seven)
        splitLabel = new Label("Split distance:");
        splitLabel.setFont(calcFont("Arial", FontWeight.BOLD, 13));
        splitLabel.setTextFill(Color.DARKSLATEBLUE);

        splitSpinner = new Spinner<>(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 6, 3));
        splitSpinner.setPrefWidth(calcSize(70));
        splitSpinner.setEditable(true);

        splitButton = new Button("✂  Apply Split");
        styleActionButton(splitButton, "#1976d2", "#0d47a1");
        splitButton.setVisible(false);

        HBox splitRow = new HBox(calcSize(8), splitLabel, splitSpinner, splitButton);
        splitRow.setAlignment(Pos.CENTER_LEFT);
        splitRow.setVisible(false);
        splitRow.setManaged(false);
        splitRow.setId("splitRow");

        actionPanel.getChildren().addAll(discardButton, splitRow);
        gameBoard.getChildren().add(actionPanel);
    }

    private void styleActionButton(Button btn, String normalColor, String hoverColor) {
        String base = "-fx-background-color: " + normalColor + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: " + (int) calcSize(13) + "px; " +
                "-fx-padding: " + (int) calcSize(6) + " " + (int) calcSize(18) + "; " +
                "-fx-background-radius: " + (int) calcSize(6) + ";";
        String hover = "-fx-background-color: " + hoverColor + "; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: " + (int) calcSize(13) + "px; " +
                "-fx-padding: " + (int) calcSize(6) + " " + (int) calcSize(18) + "; " +
                "-fx-background-radius: " + (int) calcSize(6) + ";";
        btn.setStyle(base);
        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(base));
    }

    /**
     * Shows the action panel with the correct buttons based on card type.
     *
     * @param cardType  "TEN", "QUEEN", "SEVEN", or "" (hide)
     * @param canSplit  true only when exactly 2 human marbles are on the track
     */
    public void showActionPanel(String cardType, boolean canSplit) {
        HBox splitRow = (HBox) actionPanel.lookup("#splitRow");

        discardButton.setVisible(false);
        if (splitRow != null) {
            splitRow.setVisible(false);
            splitRow.setManaged(false);
        }
        splitButton.setVisible(false);

        switch (cardType) {
            case "TEN":
                discardButton.setText("🗑  Discard Opponent's Card (Ten)");
                discardButton.setVisible(true);
                actionPanel.setVisible(true);
                break;
            case "QUEEN":
                discardButton.setText("🗑  Discard Opponent's Card (Queen)");
                discardButton.setVisible(true);
                actionPanel.setVisible(true);
                break;
            case "DISCARD_ANY":
                discardButton.setText("🗑  Discard This Card (No Marbles Out)");
                discardButton.setVisible(true);
                actionPanel.setVisible(true);
                break;
            case "SEVEN":
                if (canSplit && splitRow != null) {
                    splitRow.setVisible(true);
                    splitRow.setManaged(true);
                    splitButton.setVisible(true);
                    actionPanel.setVisible(true);
                } else {
                    // Seven acts as normal card, no split needed
                    actionPanel.setVisible(false);
                }
                break;
            default:
                actionPanel.setVisible(false);
                break;
        }
    }

    /** Returns the split distance currently selected in the spinner (1-6). */
    public int getSplitDistance() {
        return splitSpinner.getValue();
    }

    // ─── updateAllComponents ────────────────────────────────────────────────────

    public void updateAllComponents() {
        if (root != null) {
            root.setPrefWidth(screenWidth.get());
            root.setPrefHeight(screenHeight.get());
        }

        octagon.setLayoutX(calcX(748.0));
        octagon.setLayoutY(calcY(680.0));
        octagon.setScaleX(calcSize(1.5));
        octagon.setScaleY(calcSize(1.5));
        octagon.setTranslateX(calcX(120.0));

        c.updateDimensions(screenWidth.get(), screenHeight.get());
        c.setScaleX(calcSize(1.4));
        c.setScaleY(calcSize(1.3));
        c.setTranslateX(calcX(120.0));
        c.setLayoutX(calcX(300.0));
        c.setLayoutY(calcY(170.0));

        firePit.setX(calcX(648.0));
        firePit.setY(calcY(362.0));
        firePit.setWidth(calcSize(152.0));
        firePit.setHeight(calcSize(206.0));
        firePitLabel.setLayoutX(calcX(648.0));
        firePitLabel.setLayoutY(calcY(362.0));
        firePitLabel.setPrefWidth(calcSize(152.0));
        firePitLabel.setPrefHeight(calcSize(206.0));
        firePitLabel.setFont(calcFont("Arial Bold", FontWeight.BOLD, 18.0));
        if (firePitStack != null) {
            firePitStack.setLayoutX(calcX(648.0));
            firePitStack.setLayoutY(calcY(362.0));
            firePitStack.setPrefWidth(calcSize(152.0));
            firePitStack.setPrefHeight(calcSize(206.0));
            if (firePitCardRank != null) firePitCardRank.setFont(Font.font("Georgia", FontWeight.BOLD, calcSize(42)));
            if (firePitCardSuit != null) firePitCardSuit.setFont(Font.font("Arial", FontWeight.BOLD, calcSize(22)));
            if (firePitCardRank != null) firePitCardRank.setTranslateY(calcSize(-10));
            if (firePitCardSuit != null) firePitCardSuit.setTranslateY(calcSize(22));
            if (firePitCardName != null) firePitCardName.setTranslateY(calcSize(-8));
        }

        humanLabel.setLayoutX(calcX(500.0));
        humanLabel.setTranslateX(calcX(120.0));
        humanLabel.setLayoutY(calcY(900.0));
        humanLabel.setFont(calcFont(51.0));

        cpu3Label.setLayoutX(calcX(1080.0));
        cpu3Label.setLayoutY(calcY(450.0));
        cpu3Label.setFont(calcFont(42.0));

        cpu1Label.setLayoutX(calcX(180.0));
        cpu1Label.setLayoutY(calcY(450.0));
        cpu1Label.setFont(calcFont(42.0));

        cpu2Label.setLayoutX(calcX(500.0));
        cpu2Label.setTranslateX(calcX(120.0));
        cpu2Label.setLayoutY(calcY(20.0));
        cpu2Label.setFont(calcFont(42.0));

        controlGrid.setLayoutX(calcX(1270.0));
        controlGrid.setLayoutY(calcY(600.0));
        controlGrid.setHgap(calcSize(14));

        actionPanel.setLayoutX(calcX(1270.0));
        actionPanel.setLayoutY(calcY(770.0));

        remainingCards1.setLayoutX(calcX(1111.0));
        remainingCards1.setLayoutY(calcY(16.0));
        remainingCards1.setFont(calcFont(25.0));

        remainingCards2.setLayoutX(calcX(50.0));
        remainingCards2.setLayoutY(calcY(854.0));
        remainingCards2.setFont(calcFont(25.0));

        remainingCards3.setLayoutX(calcX(48.0));
        remainingCards3.setLayoutY(calcY(14.0));
        remainingCards3.setFont(calcFont(25.0));

        playButton.setLayoutX(calcX(1481.0));
        playButton.setLayoutY(calcY(520.0));
        playButton.setPrefHeight(calcSize(69.0));
        playButton.setPrefWidth(calcSize(104.0));
        playButton.setFont(calcFont(24.0));

        currentPlayerLabel.setLayoutX(calcX(1300.0));
        currentPlayerLabel.setLayoutY(calcY(121.0));
        currentPlayerLabel.setFont(calcFont("Arial", FontWeight.BOLD, 24));

        nextPlayerLabel.setLayoutX(calcX(1300.0));
        nextPlayerLabel.setLayoutY(calcY(175.0));
        nextPlayerLabel.setFont(calcFont("Arial", FontWeight.BOLD, 24));

        if (deckShadow1 != null) {
            deckShadow1.setX(calcX(1462.0) + calcSize(6));
            deckShadow1.setY(calcY(285.0) + calcSize(6));
            deckShadow1.setWidth(calcSize(152.0));
            deckShadow1.setHeight(calcSize(206.0));
        }
        if (deckShadow2 != null) {
            deckShadow2.setX(calcX(1462.0) + calcSize(3));
            deckShadow2.setY(calcY(285.0) + calcSize(3));
            deckShadow2.setWidth(calcSize(152.0));
            deckShadow2.setHeight(calcSize(206.0));
        }
        deckRect.setX(calcX(1462.0));
        deckRect.setY(calcY(285.0));
        deckRect.setWidth(calcSize(152.0));
        deckRect.setHeight(calcSize(206.0));

        deckLabel.setLayoutX(calcX(1462.0));
        deckLabel.setLayoutY(calcY(285.0));
        deckLabel.setPrefWidth(calcSize(152.0));
        deckLabel.setPrefHeight(calcSize(130.0));
        deckLabel.setFont(calcFont("Georgia", FontWeight.BOLD, 18.0));

        deckRemainingLabel.setLayoutX(calcX(1462.0));
        deckRemainingLabel.setLayoutY(calcY(380.0));
        deckRemainingLabel.setPrefWidth(calcSize(152.0));
        deckRemainingLabel.setFont(calcFont("Georgia", FontWeight.BOLD, 18.0));

        miscLabel.setLayoutX(calcX(1220.0));
        miscLabel.setLayoutY(calcY(350.0));
        miscLabel.setFont(calcFont(14.0));
        miscLabel.setPrefWidth(calcSize(220));
        miscLabel.setPrefHeight(calcSize(120));

        miscCircles[0].setCenterX(calcX(780.0));
        miscCircles[0].setCenterY(calcY(50.0));
        miscCircles[0].setRadius(calcSize(16.0));

        miscCircles[1].setCenterX(calcX(1160.0));
        miscCircles[1].setCenterY(calcY(589.0));
        miscCircles[1].setRadius(calcSize(16.0));

        miscCircles[2].setCenterX(calcX(850.0));
        miscCircles[2].setCenterY(calcY(950.0));
        miscCircles[2].setRadius(calcSize(16.0));

        miscCircles[3].setCenterX(calcX(260.0));
        miscCircles[3].setCenterY(calcY(550.0));
        miscCircles[3].setRadius(calcSize(16.0));
    }

    // ─── Rest of helpers (unchanged from original) ───────────────────────────────

    private Polygon createOctagon() {
        Polygon polygon = new Polygon();
        // Walnut wood grain — vertical grain like reference image
        // Base: warm mid-brown #8B6F52, grain lines: darker #6B5040 / lighter #A8825E
        LinearGradient woodGrain = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0.00, Color.web("#9C7A5A")),
            new Stop(0.08, Color.web("#8B6A4A")),
            new Stop(0.16, Color.web("#7A5A3C")),
            new Stop(0.24, Color.web("#9E7D5C")),
            new Stop(0.32, Color.web("#8B6A4A")),
            new Stop(0.40, Color.web("#7A5A3C")),
            new Stop(0.48, Color.web("#A08060")),
            new Stop(0.56, Color.web("#8B6A4A")),
            new Stop(0.64, Color.web("#7A5A3C")),
            new Stop(0.72, Color.web("#9E7D5C")),
            new Stop(0.80, Color.web("#8B6A4A")),
            new Stop(0.88, Color.web("#7A5A3C")),
            new Stop(1.00, Color.web("#9C7A5A")));
        polygon.setFill(woodGrain);
        polygon.setStroke(Color.web("#4A2E12"));
        polygon.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
        polygon.setStrokeWidth(calcSize(7.0));
        // Add wood texture drop-shadow for depth
        javafx.scene.effect.DropShadow woodShadow = new javafx.scene.effect.DropShadow();
        woodShadow.setColor(Color.web("#2A1005", 0.7));
        woodShadow.setRadius(calcSize(20));
        woodShadow.setOffsetX(calcSize(4));
        woodShadow.setOffsetY(calcSize(4));
        polygon.setEffect(woodShadow);
        polygon.getPoints().addAll(
                151.45, -175.73, 63.58, 36.40, -148.55, 124.27, -360.68, 36.40,
                -448.55, -175.73, -360.68, -387.86, -148.55, -475.73, 63.58, -387.86);
        polygon.setRotate(67.5);
        polygon.setScaleX(calcSize(1.5));
        polygon.setScaleY(calcSize(1.5));
        polygon.setTranslateX(calcX(120.0));
        polygon.setLayoutX(calcX(748.0));
        polygon.setLayoutY(calcY(680.0));
        return polygon;
    }

    private Rectangle createFirePit() {
        Rectangle rect = new Rectangle(calcX(648.0), calcY(362.0), calcSize(152.0), calcSize(206.0));
        rect.setArcHeight(calcSize(16.0));
        rect.setArcWidth(calcSize(16.0));
        // Warm parchment gradient — complements the walnut wood board
        LinearGradient pitGrad = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0.0,  Color.web("#FDF6E3")),
            new Stop(0.4,  Color.web("#F5E6C8")),
            new Stop(0.8,  Color.web("#EDD9A3")),
            new Stop(1.0,  Color.web("#E8CFB8")));
        rect.setFill(pitGrad);
        rect.setStroke(Color.web("#7A4F2D"));
        rect.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
        rect.setStrokeWidth(calcSize(4.0));
        javafx.scene.effect.DropShadow shadow = new javafx.scene.effect.DropShadow();
        shadow.setColor(Color.web("#5A3015", 0.55));
        shadow.setRadius(calcSize(16));
        shadow.setOffsetX(calcSize(3));
        shadow.setOffsetY(calcSize(3));
        rect.setEffect(shadow);
        return rect;
    }

    private Label createFirePitLabel() {
        // Kept for API compatibility but hidden — firePitStack is used instead
        Label label = new Label("");
        label.setVisible(false);
        return label;
    }

    private StackPane createFirePitStack() {
        StackPane stack = new StackPane();
        stack.setLayoutX(calcX(648.0));
        stack.setLayoutY(calcY(362.0));
        stack.setPrefWidth(calcSize(152.0));
        stack.setPrefHeight(calcSize(206.0));

        // 🔥 title at the top
        Label titleLbl = new Label("🔥  FirePit");
        titleLbl.setFont(calcFont("Georgia", FontWeight.BOLD, 12.0));
        titleLbl.setTextFill(Color.web("#6B3A1F"));
        StackPane.setAlignment(titleLbl, Pos.TOP_CENTER);
        titleLbl.setTranslateY(calcSize(7));

        // Divider line visual (thin rect)
        javafx.scene.shape.Rectangle divider = new javafx.scene.shape.Rectangle(calcSize(110), calcSize(1.5));
        divider.setFill(Color.web("#C4A97A"));
        StackPane.setAlignment(divider, Pos.TOP_CENTER);
        divider.setTranslateY(calcSize(30));

        // Big rank in center
        firePitCardRank = new Label("—");
        firePitCardRank.setFont(Font.font("Georgia", FontWeight.BOLD, calcSize(44)));
        firePitCardRank.setTextFill(Color.web("#2C1A0E"));
        StackPane.setAlignment(firePitCardRank, Pos.CENTER);
        firePitCardRank.setTranslateY(calcSize(-12));

        // Suit symbol below rank
        firePitCardSuit = new Label("");
        firePitCardSuit.setFont(Font.font("Arial", FontWeight.BOLD, calcSize(24)));
        firePitCardSuit.setTextFill(Color.web("#2C1A0E"));
        StackPane.setAlignment(firePitCardSuit, Pos.CENTER);
        firePitCardSuit.setTranslateY(calcSize(22));

        // Card name at bottom
        firePitCardName = new Label("Empty");
        firePitCardName.setFont(calcFont("Georgia", FontWeight.BOLD, 10.0));
        firePitCardName.setTextFill(Color.web("#7A5A3C"));
        firePitCardName.setWrapText(true);
        firePitCardName.setTextAlignment(TextAlignment.CENTER);
        firePitCardName.setMaxWidth(calcSize(130));
        StackPane.setAlignment(firePitCardName, Pos.BOTTOM_CENTER);
        firePitCardName.setTranslateY(calcSize(-10));

        stack.getChildren().addAll(titleLbl, divider, firePitCardRank, firePitCardSuit, firePitCardName);
        return stack;
    }

    private Label createHumanLabel() {
        Label label = new Label("Human");
        label.setLayoutX(calcX(500.0));
        label.setTranslateX(calcX(120.0));
        label.setLayoutY(calcY(900.0));
        label.setPrefHeight(calcSize(99.0));
        label.setPrefWidth(calcSize(193.0));
        label.setFont(calcFont(51.0));
        return label;
    }

    private Label createCPU3Label() {
        Label label = new Label("CPU 3");
        label.setLayoutX(calcX(1080.0));
        label.setLayoutY(calcY(450));
        label.setPrefHeight(calcSize(62.0));
        label.setPrefWidth(calcSize(163.0));
        label.setRotate(90.0);
        label.setFont(calcFont(42.0));
        return label;
    }

    private Label createCPU1Label() {
        Label label = new Label("CPU 1");
        label.setLayoutX(calcX(180.0));
        label.setLayoutY(calcY(450.0));
        label.setPrefHeight(calcSize(62.0));
        label.setPrefWidth(calcSize(163.0));
        label.setRotate(90.0);
        label.setFont(calcFont(42.0));
        return label;
    }

    private Label createCPU2Label() {
        Label label = new Label("CPU 2");
        label.setLayoutX(calcX(500.0));
        label.setTranslateX(calcX(120.0));
        label.setLayoutY(calcY(20.0));
        label.setPrefHeight(calcSize(62.0));
        label.setPrefWidth(calcSize(163.0));
        label.setFont(calcFont(42.0));
        return label;
    }

    Label createRemainingCardsLabel(String s, double x, double y) {
        Label label = new Label(s);
        label.setLayoutX(x);
        label.setLayoutY(y);
        label.setFont(calcFont(25.0));
        return label;
    }

    public void initializePlayButtonHandler() {
        playButton.setOnMouseClicked(new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                // Actual play logic is set in Main after game initialisation
            }
        });
    }

    public Button createPlayButton() {
        Button button = new Button("Play");
        button.setLayoutX(calcX(1481.0));
        button.setLayoutY(calcY(520.0));
        button.setMnemonicParsing(false);
        button.setPrefHeight(calcSize(69.0));
        button.setPrefWidth(calcSize(104.0));
        button.setFont(calcFont(24.0));
        button.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-font-weight: bold;");
        return button;
    }

    private Label createCurrentPlayerLabel() {
        Label label = new Label("Current player is:= x");
        label.setLayoutX(calcX(1300.0));
        label.setLayoutY(calcY(121.0));
        label.setPrefHeight(calcSize(52.0));
        label.setPrefWidth(calcSize(230.0));
        label.setMinWidth(calcSize(500));
        label.setFont(calcFont("Arial", FontWeight.BOLD, 24));
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private Label createNextPlayerLabel() {
        Label label = new Label("Next player is:= x");
        label.setLayoutX(calcX(1300.0));
        label.setLayoutY(calcY(175.0));
        label.setPrefHeight(calcSize(52.0));
        label.setPrefWidth(calcSize(212.0));
        label.setMinWidth(calcSize(500));
        label.setFont(calcFont("Arial", FontWeight.BOLD, 24));
        label.setAlignment(Pos.CENTER);
        return label;
    }

    private Rectangle createDeckShadow(double offX, double offY) {
        Rectangle rect = new Rectangle(
            calcX(1462.0) + offX, calcY(285.0) + offY,
            calcSize(152.0), calcSize(206.0));
        rect.setArcHeight(calcSize(14.0));
        rect.setArcWidth(calcSize(14.0));
        rect.setFill(Color.web("#5A3015"));
        rect.setStroke(Color.web("#C4A97A", 0.3));
        rect.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
        rect.setStrokeWidth(calcSize(2.0));
        rect.setOpacity(0.55);
        return rect;
    }

    private Rectangle createDeckRect() {
        Rectangle rect = new Rectangle(calcX(1462.0), calcY(285.0), calcSize(152.0), calcSize(206.0));
        rect.setArcHeight(calcSize(14.0));
        rect.setArcWidth(calcSize(14.0));
        // Dark walnut gradient — same family as the board, deeper/richer
        LinearGradient deckGrad = new LinearGradient(0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
            new Stop(0.00, Color.web("#4A2C0F")),
            new Stop(0.20, Color.web("#5C3A1A")),
            new Stop(0.40, Color.web("#3D2208")),
            new Stop(0.60, Color.web("#6B4520")),
            new Stop(0.80, Color.web("#4A2C0F")),
            new Stop(1.00, Color.web("#3D2208")));
        rect.setFill(deckGrad);
        rect.setStroke(Color.web("#C4A97A"));
        rect.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
        rect.setStrokeWidth(calcSize(4.0));
        javafx.scene.effect.DropShadow deckShadow = new javafx.scene.effect.DropShadow();
        deckShadow.setColor(Color.web("#1A0A00", 0.7));
        deckShadow.setRadius(calcSize(14));
        deckShadow.setOffsetX(calcSize(3));
        deckShadow.setOffsetY(calcSize(3));
        rect.setEffect(deckShadow);
        return rect;
    }

    private Label createDeckLabel() {
        Label label = new Label("♠ ♥\n Deck \n♣ ♦");
        label.setLayoutX(calcX(1462.0));
        label.setLayoutY(calcY(285.0));
        label.setPrefWidth(calcSize(152.0));
        label.setPrefHeight(calcSize(130.0));
        label.setAlignment(Pos.CENTER);
        label.setTextAlignment(TextAlignment.CENTER);
        label.setTextFill(Color.web("#E8C97A"));
        label.setFont(calcFont("Georgia", FontWeight.BOLD, 18.0));
        label.setWrapText(true);
        return label;
    }

    private Label createDeckRemainingLabel() {
        Label label = new Label("Rem: x");
        label.setLayoutX(calcX(1462.0));
        label.setLayoutY(calcY(380.0));
        label.setPrefWidth(calcSize(152.0));
        label.setPrefHeight(calcSize(60.0));
        label.setAlignment(Pos.CENTER);
        label.setMinWidth(calcSize(152.0));
        label.setTextFill(Color.web("#C4A97A"));
        label.setFont(calcFont("Georgia", FontWeight.BOLD, 18.0));
        return label;
    }

    private Label createMiscLabel() {
        Label label = new Label("Welcome! Select a card and marble to play.");
        label.setLayoutX(calcX(1220.0));
        label.setLayoutY(calcY(350.0));
        label.setFont(calcFont(14.0));
        label.setAlignment(Pos.TOP_LEFT);
        label.setWrapText(true);
        label.setPrefWidth(calcSize(220));
        label.setPrefHeight(calcSize(120));
        label.setPadding(new Insets(calcSize(8)));
        label.setBackground(new Background(new BackgroundFill(
                Color.web("#1a1a2e", 0.85), new CornerRadii(calcSize(8)), Insets.EMPTY)));
        label.setBorder(new Border(new BorderStroke(
                Color.web("#4a90d9"), BorderStrokeStyle.SOLID, new CornerRadii(calcSize(8)), new BorderWidths(calcSize(1.5)))));
        label.setTextFill(Color.web("#e0e8ff"));
        return label;
    }

    private Circle createMiscCircle(double x, double y) {
        Circle circle = new Circle(x, y, calcSize(16.0), Color.web("#fad180"));
        circle.setStroke(Color.BLACK);
        circle.setStrokeType(javafx.scene.shape.StrokeType.INSIDE);
        return circle;
    }

    // Alert helpers (kept exactly as before)
    public void displayAlert1(String title, String message) throws FileNotFoundException {
        Stage window = new Stage();
        ImageView warningIcon = new ImageView(new Image(new FileInputStream("Warning.png")));
        warningIcon.setFitWidth(calcSize(70));
        warningIcon.setFitHeight(calcSize(70));
        VBox layout = new VBox(calcSize(20));
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(calcSize(20)));
        layout.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(calcSize(10)), Insets.EMPTY)));
        layout.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(calcSize(10)), new BorderWidths(1))));
        layout.getChildren().add(warningIcon);
        Label titleLabel = new Label(title);
        titleLabel.setFont(calcFont("Arial", FontWeight.BOLD, 35));
        titleLabel.setTextFill(Color.DARKSLATEBLUE);
        HBox contentBox = new HBox(calcSize(10));
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(titleLabel, warningIcon);
        Label messageLabel = new Label(message);
        messageLabel.setFont(calcFont(20));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(calcSize(500));
        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        okButton.setOnAction(e -> window.close());
        layout.getChildren().addAll(contentBox, messageLabel, okButton);
        Scene scene = new Scene(layout);
        scene.setFill(Color.TRANSPARENT);
        window.setScene(scene);
        window.showAndWait();
    }

    public void displayAlert2(String title, String message) {
        Stage window = new Stage();
        VBox layout = new VBox(calcSize(20));
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(calcSize(20)));
        layout.setBackground(new Background(new BackgroundFill(Color.WHITE, new CornerRadii(calcSize(10)), Insets.EMPTY)));
        layout.setBorder(new Border(new BorderStroke(Color.LIGHTGRAY, BorderStrokeStyle.SOLID, new CornerRadii(calcSize(10)), new BorderWidths(1))));
        Label titleLabel = new Label(title);
        titleLabel.setFont(calcFont("Arial", FontWeight.BOLD, 35));
        titleLabel.setTextFill(Color.DARKSLATEBLUE);
        Label messageLabel = new Label(message);
        messageLabel.setFont(calcFont(20));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(calcSize(500));
        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
        okButton.setOnAction(e -> window.close());
        layout.getChildren().addAll(titleLabel, messageLabel, okButton);
        Scene scene = new Scene(layout);
        scene.setFill(Color.TRANSPARENT);
        window.setScene(scene);
        window.showAndWait();
    }

    // ─── Public int selectedCount (kept for backward compat) ────────────────────
    public int selectedCount;
}