package cr.ac.una.muffetsolitario.controller;

import java.net.URL;
import java.util.ResourceBundle;

import cr.ac.una.muffetsolitario.model.UserAccountDto;
import cr.ac.una.muffetsolitario.service.UserAccountService;
import cr.ac.una.muffetsolitario.util.AnimationHandler;
import cr.ac.una.muffetsolitario.util.FlowController;
import cr.ac.una.muffetsolitario.util.Respuesta;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleIntegerProperty;
import io.github.palexdev.materialfx.controls.MFXButton;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

public class RankingsController extends Controller implements Initializable {

    @FXML private AnchorPane root;
    @FXML private ImageView imgBackground;
    
    @FXML private MFXButton btnSalir;
    @FXML private Label lblRankingsTitle;
    @FXML private TableColumn<UserAccountDto, Integer> tbcRanking;
    @FXML private TableColumn<UserAccountDto, Integer> tbcUserPoints;
    @FXML private TableColumn<UserAccountDto, String> tbcUsername;
    @FXML private TableColumn<UserAccountDto, Integer> tbcUserPlayedGames;
    @FXML private TableView<UserAccountDto> tbvRankings;

    private final AnimationHandler animationHandler = AnimationHandler.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        imgBackground.fitHeightProperty().bind(root.heightProperty());
        imgBackground.fitWidthProperty().bind(root.widthProperty());

        startGlitchEffect(lblRankingsTitle, true);
        startGlitchEffect(btnSalir, false);
        setupTableColumns();
        loadAndDisplayRankings();
    }

    @Override
    public void initialize() {
        // Required by abstract Controller, not used in this controller
    }

    /**
     * Starts a random glitch effect for the given node (Label or Button).
     * Each node gets its own Timeline.
     */
    private void startGlitchEffect(javafx.scene.Node node, boolean isTitle) {
        Timeline glitchTimeline = new Timeline();
        glitchTimeline.setCycleCount(Timeline.INDEFINITE);

        glitchTimeline.getKeyFrames().add(
            new KeyFrame(Duration.seconds(1), event -> {
                double nextDelay = 1.0 + Math.random() * 8;
                glitchTimeline.stop();
                animationHandler.playHitEffect(node);
                glitchTimeline.getKeyFrames().set(0,
                    new KeyFrame(Duration.seconds(nextDelay), ev -> handleGlitch(node, glitchTimeline)));
                glitchTimeline.playFromStart();
            })
        );
        glitchTimeline.play();
    }

    private void handleGlitch(javafx.scene.Node node, Timeline glitchTimeline) {
        animationHandler.playHitEffect(node);
    }

    /**
     * Configures the TableView columns to display the correct UserAccountDto properties.
     */
    private void setupTableColumns() {
        // The ranking column will be set dynamically based on the row index
        tbcRanking.setCellValueFactory(cellData -> {
            int index = tbvRankings.getItems().indexOf(cellData.getValue());
            return new SimpleIntegerProperty(index + 1).asObject();
        });
        tbcUserPoints.setCellValueFactory(cellData -> cellData.getValue().userTotalScoreProperty().asObject());
        tbcUsername.setCellValueFactory(cellData -> cellData.getValue().userNicknameProperty());
        tbcUserPlayedGames.setCellValueFactory(cellData -> cellData.getValue().userTotalGamesProperty().asObject());
    }

    /**
     * Loads all users from the database, sorts them by points, and displays them in the TableView.
     */
    private void loadAndDisplayRankings() {
        UserAccountService userService = new UserAccountService();
        Respuesta respuesta = userService.getAllUsers();
        if (!respuesta.getEstado()) {
            tbvRankings.setItems(FXCollections.observableArrayList());
            return;
        }
        @SuppressWarnings("unchecked")
        ObservableList<UserAccountDto> users = FXCollections.observableArrayList((java.util.List<UserAccountDto>) respuesta.getResultado("Users"));
        users.sort((u1, u2) -> Integer.compare(u2.getUserTotalScore(), u1.getUserTotalScore()));
        tbvRankings.setItems(users);
    }

    @FXML
    void onActionBtnSalir(ActionEvent event) {
        FlowController.getInstance().goView("LogInView");
    }
}