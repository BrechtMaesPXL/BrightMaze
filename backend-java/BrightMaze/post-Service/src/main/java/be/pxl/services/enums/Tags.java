package be.pxl.services.enums;

/**
 * Enum representing popular YouTube tags (prefixed with "#").
 */
public enum Tags {
    MUSIC("#music"),
    VLOG("#vlog"),
    TUTORIAL("#tutorial"),
    GAMING("#gaming"),
    REVIEW("#review"),
    HOWTO("#howto"),
    FUNNY("#funny"),
    TRAVEL("#travel"),
    NEWS("#news"),
    COMEDY("#comedy"),
    LIVE("#live"),
    CHALLENGE("#challenge"),
    FASHION("#fashion"),
    BEAUTY("#beauty"),
    FITNESS("#fitness"),
    FOOD("#food"),
    MOTIVATION("#motivation"),
    EDUCATION("#education"),
    SPORTS("#sports"),
    DIY("#diy"),
    UNBOXING("#unboxing"),
    REACTION("#reaction"),
    ASMR("#asmr"),
    TECH("#tech"),
    ART("#art"),
    SCIENCE("#science"),
    KIDS("#kids"),
    PETS("#pets"),
    NATURE("#nature"),
    MOVIE("#movie"),
    TRAILER("#trailer"),
    SHORTS("#shorts"),
    ANIME("#anime"),
    DRAMA("#drama"),
    ROMANCE("#romance"),
    HISTORY("#history"),
    DOCUMENTARY("#documentary"),
    CELEBRITY("#celebrity"),
    MYSTERY("#mystery"),
    TECHNOLOGY("#technology"),
    GAMER("#gamer"),
    COOKING("#cooking"),
    TRAVELING("#traveling"),
    LIFESTYLE("#lifestyle"),
    MUSICVIDEO("#musicvideo"),
    FESTIVAL("#festival"),
    FASHIONSTYLE("#fashionstyle"),
    PHOTOGRAPHY("#photography"),
    COMEDYCLUB("#comedyclub"),
    ENTERTAINMENT("#entertainment");

    private final String tag;

    Tags(String tag) {
        this.tag = tag;
    }

    /**
     * Returns the string representation of the tag, including the leading '#'.
     *
     * @return the tag string
     */
    public String getValue() {
        return tag;
    }

    @Override
    public String toString() {
        return tag;
    }
}
