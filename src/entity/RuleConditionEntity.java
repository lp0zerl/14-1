package entity;

import java.util.ArrayList;
import java.util.List;

@Embeddable
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
class RuleConditionEntity {
    @Column(name = "query_type", nullable = false)
    @JsonProperty("query")
    private String queryType;
    @Type(type = "jsonb")
    @Column(name = "arguments", columnDefinition = "jsonb")
    @JsonProperty("arguments")
    private List<String> arguments = new ArrayList<>();
    @Column(name = "negate", nullable = false)
    @JsonProperty("negate")
    private boolean negate = false;
    @Column(name = "condition_order", nullable = false)
    @JsonIgnore
    private int order;

    public RuleConditionEntity() {
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public void setArguments(List<String> arguments) {
        this.arguments = arguments;
    }

    public boolean isNegate() {
        return negate;
    }

    public void setNegate(boolean negate) {
        this.negate = negate;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }
}
