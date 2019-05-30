package com.example.glass.gallerysample;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for the gallery extending the {@link BaseObservable} to provide data binding.
 */
public class GalleryModel extends BaseObservable {

  private List<GalleryItem> galleryItems = new ArrayList<>();

  /**
   * Value returned by this method is bound with the {@link android.widget.TextView} informing about
   * the empty gallery.
   */
  @Bindable
  public boolean isGalleryEmpty() {
    return galleryItems.isEmpty();
  }

  /**
   * Returns {@link List} of all available {@link GalleryItem} objects.
   */
  public List<GalleryItem> getItems() {
    return galleryItems;
  }

  /**
   * Adds found {@link GalleryItem} to the gallery items list.
   */
  public void addItem(GalleryItem galleryItem) {
    galleryItems.add(galleryItem);
    notifyPropertyChanged(BR.galleryEmpty);
  }

  /**
   * Clears the list of {@link GalleryItem} objects.
   */
  public void clearItems() {
    galleryItems.clear();
    notifyPropertyChanged(BR.galleryEmpty);
  }
}
