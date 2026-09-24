# Use make to rebuild assets from svg sources, if you
# change the svgs.  Be sure to commit the modified
# output files to git.  If you're not messing with
# the svgs, you shouldn't need to do anything here.

# This is a Makefile because I'm not familiar enough with
# Maven to figure out if there's a way to integrate it into
# the larger build

# No tailing slash is important here.
RESOURCES_DIR := ../src/main/resources/org/rezrov/roboworld
SVGS := $(wildcard *.svg)
PNGS := $(addprefix $(RESOURCES_DIR)/,$(SVGS:.svg=.png))
INKSCAPE := "inkscape"

# Anything named *_item.svg is an item.  Item svgs are special in that we render
# them to both the item png *and* an outline png.  Item svgs are required to
# have a path defined named "ItemOutline".  This path is used to generate
# the outline, ignoring the other contents of the svg.
PNGS += $(foreach svg,$(wildcard *_item.svg), $(RESOURCES_DIR)/$(svg:.svg=_outline.png))

ALL: $(PNGS)

# For items, we expect the svg to contain something with SVG Object ID
# of ItemOutline.  This item is exported as the outline.  Everything
# else is exported as the item.
$(RESOURCES_DIR)/%_item_outline.png: %_item.svg
	$(INKSCAPE) --actions="export-id-only;export-id:ItemOutline;export-filename:$@;export-do" $<

$(RESOURCES_DIR)/%_item.png: %_item.svg
	$(INKSCAPE) --actions="select-by-id:ItemOutline;selection-hide;export-area-drawing;export-filename:$@;export-do" $<


# Icon exports include whitespace around the edges; other exports do not.

$(RESOURCES_DIR)/%_icon.png: %_icon.svg
	$(INKSCAPE) --actions="export-area-page;export-filename:$@;export-do" $<

# Everything else just gets exported as is (cropped to content)
$(RESOURCES_DIR)/%.png: %.svg
	$(INKSCAPE) --actions="export-area-drawing;export-filename:$@;export-do" $<

