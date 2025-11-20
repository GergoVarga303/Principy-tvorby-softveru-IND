package TerraFutura;
//finished, but additional
public class ResourcePosition {
    private final Resource resource;
    private final GridPosition position;

    public ResourcePosition(Resource resource, GridPosition position) {
        this.resource = resource;
        this.position = position;
    }

        public Resource getResource() {
            return resource;
        }

        public GridPosition getPosition() {
            return position;
        }

}
